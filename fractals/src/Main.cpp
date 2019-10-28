#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include <iostream>
#include "LayerPy.h"
#include "ParametersPy.h"

typedef struct {
    PyObject_HEAD
    /* Type-specific fields go here. */
	int myVal;	
} MyData;

static PyTypeObject MyDatatype = {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.MyData",             /* tp_name */
    sizeof(MyData), /* tp_basicsize */
    0,                         /* tp_itemsize */
    0,                         /* tp_dealloc */
    0,                         /* tp_print */
    0,                         /* tp_getattr */
    0,                         /* tp_setattr */
    0,                         /* tp_reserved */
    0,                         /* tp_repr */
    0,                         /* tp_as_number */
    0,                         /* tp_as_sequence */
    0,                         /* tp_as_mapping */
    0,                         /* tp_hash  */
    0,                         /* tp_call */
    0,                         /* tp_str */
    0,                         /* tp_getattro */
    0,                         /* tp_setattro */
    0,                         /* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        /* tp_flags */
    "Noddy objects",           /* tp_doc */
};

static PyObject * create_fractal(PyObject *self, PyObject *args) {

    /*if (!PyArg_ParseTuple(args, "LL", &argument, &argument2))
       return NULL;
   
	std::cout << argument << " " << argument2 << std::endl;*/
	
	/*PyObject *myData;
    if(!PyArg_ParseTuple(args, "O!", &MyDatatype, &myData))  /* type-checked */
        return 0;
	
	/*std::cout << "C++ FUNCTION" << std::endl;*/
    
    return PyLong_FromLong(1);
}

static PyMethodDef FractalMethods[] = {
    {"render",  create_fractal, METH_VARARGS, "Render a fractal."},
    
    {NULL, NULL, 0, NULL}        /* Sentinel */
};

static struct PyModuleDef fractalModule = {
    PyModuleDef_HEAD_INIT,
    "fractal",   /* name of module */
    "some docs", /* module documentation, may be NULL */
    -1,       /* size of per-interpreter state of the module,
                 or -1 if the module keeps state in global variables. */
    FractalMethods
};	

PyMODINIT_FUNC 
PyInit_fractal(void) {
	PyObject* m;

    MyDatatype.tp_new = PyType_GenericNew;
    if (PyType_Ready(&MyDatatype) < 0)
        return NULL;

    if (PyType_Ready(&LayerType) < 0)
        return NULL;

	if (PyType_Ready(&ParametersType) < 0)
        return NULL;
	
    m = PyModule_Create(&fractalModule);
    if (m == NULL)
        return NULL;

    Py_INCREF(&MyDatatype);
    PyModule_AddObject(m, "MyData", (PyObject *)&MyDatatype);
	
	Py_INCREF(&LayerType);
    PyModule_AddObject(m, "Layer", (PyObject *)&LayerType);
	
	Py_INCREF(&ParametersType);
    PyModule_AddObject(m, "Parameters", (PyObject *)&ParametersType);
	
    return m;
}

int main(int argc, char *argv[]) {
    wchar_t *program = Py_DecodeLocale(argv[0], NULL);
    if (program == NULL) {
        fprintf(stderr, "Fatal error: cannot decode argv[0]\n");
        exit(1);
    }

    /* Add a built-in module, before Py_Initialize */
    PyImport_AppendInittab("fractal", PyInit_fractal);

    /* Pass argv[0] to the Python interpreter */
    Py_SetProgramName(program);

    /* Initialize the Python interpreter.  Required. */
    Py_Initialize();

    /* Optionally import the module; alternatively,
       import can be deferred until the embedded script
       imports it. */
    PyImport_ImportModule("fractal");

    PyMem_RawFree(program);
    return 0;
}