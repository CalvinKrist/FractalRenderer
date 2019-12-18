#pragma once

#include <Python.h>
#include "structmember.h"
#include "HistogramLayer.h"
#include "LayerPy.h"

// Python wrapper around a C++ Layer class
typedef struct {
    PyObject_HEAD
	Layer * myLayer;
	//ParameterData * parameterData;
} HistogramLayerData;

static PyObject *
HistogramLayer_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	HistogramLayerData *self;

    self = (HistogramLayerData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myLayer = new HistogramLayer();
        if (self->myLayer == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
    return (PyObject *)self;
}

static void
HistogramLayer_dealloc(HistogramLayerData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *
HistogramLayer_test(HistogramLayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    return PyFloat_FromDouble(101.1);
}

static PyMemberDef HistogramLayer_members[] = {
    {NULL}  /* Sentinel */
};

/*
static PyMethodDef HistogramLayer_methods[] = {
	Layer_methods[0],
    {"test", (PyCFunction)HistogramLayer_test, METH_NOARGS,
     "Return the opacity of the layer"
    },
    {NULL}  /* Sentinel */
//};

/*******************************************
***  Make Polymorphism work for methods  ***
********************************************/

int numHistogramMethods = 1;
int numLayerMethods = sizeof(Layer_methods) / sizeof(PyMethodDef) - 1;
static int numMethods = numLayerMethods + numHistogramMethods + 1;
static PyMethodDef* HistogramLayer_methods = new PyMethodDef[numMethods];

struct Initializer
{
    Initializer()
    {
		HistogramLayer_methods[0] = {"test", (PyCFunction)HistogramLayer_test, METH_NOARGS,
									"Return the opacity of the layer"
									};
									
		for(int i = 0; i < numLayerMethods; i++)
			HistogramLayer_methods[numHistogramMethods + i] = Layer_methods[i];

		HistogramLayer_methods[numMethods - 1] = {NULL}; /* Sentinel */
    }
};

Initializer initializer;

static PyTypeObject HistogramLayerType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.HistogramLayer",           	/* tp_name */
    sizeof(HistogramLayerData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)HistogramLayer_dealloc,  /* tp_dealloc */
    0,                         	/* tp_print */
    0,                         	/* tp_getattr */
    0,                         	/* tp_setattr */
    0,                         	/* tp_reserved */
    0,                         	/* tp_repr */
    0,                         	/* tp_as_number */
    0,                         	/* tp_as_sequence */
    0,                         	/* tp_as_mapping */
    0,                         	/* tp_hash  */
    0,                         	/* tp_call */
    0,            				/* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "HistogramLayer",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    HistogramLayer_methods,             	/* tp_methods */
    HistogramLayer_members,				/* tp_members */
    0,                         	/* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    HistogramLayer_new,                 	/* tp_new */
};