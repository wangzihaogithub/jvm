/****************************
 * file name:   class.h
 *
 * *************************/

#ifndef __CLASS__H__
#define __CLASS__H__

#include "comm.h"

typedef enum tag_value {
    CONST_Utf8			= 1,
    CONST_Integer		= 3,
    CONST_Float			= 4,
    CONST_Long			= 5,
    CONST_Double		= 6,
    CONST_Class			= 7,
    CONST_String		= 8,
    CONST_Fieldref		= 9,
    CONST_Methodref		= 10,
    CONST_IfMethodref	= 11,
    CONST_NameAndType	= 12, 
    CONST_MethodHandle	= 15,
    CONST_MethodType	= 16,
    CONST_InvokeDynamic	= 18,
} TypeTag;

typedef enum access_flags {
    ACC_PUBLIC      = 0x0001,
    ACC_PRIVATE     = 0x0002,
    ACC_PROTECTED   = 0x0004,
    ACC_STATIC      = 0x0008,
    ACC_FINAL       = 0x0010,
    ACC_SUPER       = 0x0020,
    ACC_SYNCHRONIZED= 0x0020, 
    ACC_VOLATILE    = 0x0040,
    ACC_BRIDGE      = 0x0040,
    ACC_TRANSLENT   = 0x0080,
	ACC_VARARGS		= 0x0080, 
    ACC_NATIVE      = 0x0100, 
    ACC_INTERFACE   = 0x0200,
    ACC_ABSTRACT    = 0x0400,
    ACC_STRICT      = 0x0800,
    ACC_SYNTHETIC   = 0x1000,
    ACC_ANNOTATION  = 0x2000,
    ACC_ENUM        = 0x4000,
} AccTag;

enum Class_State {
	CLASS_LOADED	= 0x01,
	CLASS_LINKED	= 0x02,
	CLASS_BAD		= 0x03,
	CLASS_INITING	= 0x04,
	CLASS_INITED	= 0x05
};

enum Prime_Class_Type {
	PRIM_VOID		= 0x00,
	PRIM_BYTE		= 0x01,
	PRIM_BOOLEAN	= 0x02,
	PRIM_CHAR		= 0x03,
	PRIM_SHORT		= 0x04,
	PRIM_INT		= 0x05,
	PRIM_FLOAT		= 0x06,
	PRIM_LONG		= 0x07,
	PRIM_DOUBLE		= 0x08
};

typedef struct ConstPoolEntry {
	U1 tag;
	union {
		struct {
			U2 name_index;
		} class_info;

		struct {
			U2 class_index;
			U2 name_type_index;
		} fieldref_info;

		struct {
			U2 class_index;
			U2 name_type_index;
		} methodref_info;

		struct {
			U2 class_index;
			U2 name_type_index;
		} ifmethodref_info;

		struct {
			U2 string_index;
		} string_info;

		struct {
			U4 bytes;
		} integer_info;

		struct {
			U4 bytes;
		} float_info;

		struct {
			U4 high_bytes;
			U4 low_bytes;
		} long_info;

		struct {
			U4 high_bytes;
			U4 low_bytes;
		} double_info;

		struct {
			U2 name_index;
			U2 type_index;
		} nametype_info;

		struct {
			U2 	 length;
			char *bytes;
		} utf8_info;

		struct {
			U1 ref_kind;
			U2 ref_index;
		} methodhandle_info;

		struct {
			U2 type_index;
		} methodtype_info;

		struct {
			U2 bootstrap_method_attr_index;
			U2 name_type_index;
		} invokedynamic_info;
	} info;
} ConstPoolEntry;

typedef struct ConstPool {
	U2 length;
	ConstPoolEntry *entries;
} ConstPool;

typedef struct ExceptionEntry {
    U2 start_pc;
    U2 end_pc;
    U2 handler_pc;
    U2 catch_type;
} ExceptionEntry;

typedef struct ExceptionTable {
	U4 length;
	struct ExceptionEntry* entries;
} ExceptionTable;

typedef struct Object Object;
typedef struct Object Class;
struct Object {
	uintptr_t lock;
	Class *cls;
}; 

typedef struct FieldEntry {
	Class *class;
	char *name;	
	char *type;
	char *signature;
	U2	acc_flags;
	U2	constant;
} FieldEntry;

// Slot store type and value
typedef struct Slot {
    TypeTag     tag;
    uintptr_t   value;
} Slot;

/*
 * SlotBuffer
 */
typedef struct SlotBuffer {
    Slot *slots;	// slot list
    U4    validCnt;	// valid slot count
	U4	  capacity;	// capacity of slot list
	U1	  use;		// 1 means in use, 0 means free
} SlotBuffer;

typedef SlotBuffer LocalVarTable;
typedef SlotBuffer OperandStack;

/**
 * SlotBufferPool
 */
typedef struct SlotBufferPool {
	SlotBuffer *buffers;
	U4			capacity;
} SlotBufferPool;

typedef struct StackFrame {
	LocalVarTable *localTbl;
	OperandStack  *opdStack;
	ConstPool	  *constPool;
} StackFrame;

/*
 * //OPTIMIZE
 * Stack frame pool for reUse Stack frame.
 * using item is in front & free item is in back.
 * Not really free item to avoid memory fragment
 */
typedef struct StackFramePool {
	U4			poolSize;
	U4			activeCnt;
	StackFrame	*elements;
} StackFramePool;

typedef struct MethodEntry {
	Class           *class;
	char            *name;
	char            *type;
	char            *signature; 
	U2	            acc_flags;
	U2				max_stack;
	U2				max_locals;
	U2	            args_count;
	U4	            code_length;
	void            *code;
	ExceptionTable  excep_tbl;
} MethodEntry;

#define RESERVE_SIZE 4
typedef struct ClassEntry {
	uintptr_t reserve[RESERVE_SIZE];
	char *name;
	char *signature;
	char *super_name;
	char *source_file;
	Class *super;
	U1 state;
	U2 acc_flags;
	U2 fields_count;
	FieldEntry  *fields;
	ConstPool *constPool;
	U2 methods_count;
	MethodEntry *methods;
	U2 interfaces_count;
	Class **interfaces;
	U2 *interfaces_index;
	Object *class_loader;
} ClassEntry;

#define CLASS_CE(cls) ( (ClassEntry *) (cls + 1) )
#define CE_CLASS(clsEntry) ( (Class *) clsEntry )

extern Class* allocClass();
extern ConstPool* newConstPool(int length);
extern Class* defineClass(const char *clsname, const char *data, int len);
extern void linkClass(Class *class);
extern Class* initClass(Class *class);
extern Class* findSystemClass(char *classname);

extern FieldEntry* findField(Class *class, char *name, char *type);
extern MethodEntry* findMethod(Class *class, char *name, char *type);
extern MethodEntry* lookupVirtualMethod(Class *class, char *name, char *type);

/*
 * Load a Class from a .class file
 * Parameters:
 *		path:		path of *.class
 *		classname:	such as System (may represent java.lang.System)
 * Return:
 *		if Error: NULL
 *		if OK	: base address of Class
 */
extern Class* loadClassFromFile(char *path, char *classname);

/*
 * Load a Class from a .jar file
 * Parameters:
 *		path:		path of *.jar (such as /jdk/jre/lib/rt.jar)
 * Return:
 *		if Error: NULL
 *		if OK	: base address of Class
 */

extern int loadClassFromJar(char *path, Class ***classes);

/**
 * Log the information of ClassEntry
 */
extern void logClassEntry(ClassEntry* clsEntry);

/*
 * Create a specified capability SlotBufferPool
 */
extern int createSlotBufferPool(int cap);

/*
 * Destroy SlotBufferPool
 */
extern void destroySlotBufferPool();

/*
 * Obtain a SlotBuffer.
 * BE CAREFUL: call recycleSlotBuffer to release
 */
extern SlotBuffer* obtainSlotBuffer();

extern SlotBuffer* obtainCapSlotBuffer(int cap);

/*
 * Recyle SlotBuffer for reuse.
 */
extern void recycleSlotBuffer(SlotBuffer* slotbuf);

/*
 * Ensure SlotBuffer capability
 */
extern int ensureSlotBufferCap(SlotBuffer* buffer, int count);

#endif
