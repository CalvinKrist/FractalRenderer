#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include <iostream>
#include "HistogramLayerPy.h"
#include "PalettePy.h"
#include "FractalPy.h"

static PyMethodDef FractalMethods[] = {    
    {NULL, NULL, 0, NULL}        /* Sentinel */
};

static struct PyModuleDef fractalModule = {
    PyModuleDef_HEAD_INIT,
    "fractal",   /* name of module */
    "some docss", /* module documentation, may be NULL */
    -1,       /* size of per-interpreter state of the module,
                 or -1 if the module keeps state in global variables. */
    FractalMethods
};	

PyMODINIT_FUNC 
PyInit_fractal(void) {
	PyObject* m;

    if (PyType_Ready(&FractalType) < 0)
        return NULL;
	
	if (PyType_Ready(&LayerType) < 0)
        return NULL;
	
	if (PyType_Ready(&HistogramLayerType) < 0)
        return NULL;
	
	if (PyType_Ready(&PaletteType) < 0)
        return NULL;
	
    m = PyModule_Create(&fractalModule);
    if (m == NULL)
        return NULL;
	
	Py_INCREF(&FractalType);
    PyModule_AddObject(m, "Fractal", (PyObject *)&FractalType);
	
	Py_INCREF(&LayerType);
    PyModule_AddObject(m, "Layer", (PyObject *)&LayerType);
	
	Py_INCREF(&HistogramLayerType);
    PyModule_AddObject(m, "HistogramLayer", (PyObject *)&HistogramLayerType);
	
	Py_INCREF(&PaletteType);
    PyModule_AddObject(m, "Palette", (PyObject *)&PaletteType);
	
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