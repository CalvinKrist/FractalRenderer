#pragma once

#include <Python.h>
#include "structmember.h"
#include "SimpleBandsLayer.h"
#include "LayerPy.h"

// Python wrapper around a C++ Layer class
typedef struct {
    PyObject_HEAD
	Layer * myLayer;
	//ParameterData * parameterData;
} SimpleBandsLayerData;

static PyObject *
SimpleBandsLayer_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	SimpleBandsLayerData *self;

    self = (SimpleBandsLayerData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myLayer = new SimpleBandsLayer();
        if (self->myLayer == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
    return (PyObject *)self;
}

static void
SimpleBandsLayer_dealloc(SimpleBandsLayerData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *  
SimpleBandsLayer_toString(SimpleBandsLayerData* self) {
	std::string description = self->myLayer->toString();
	return PyUnicode_FromFormat(description.c_str()); 
}

static PyMemberDef SimpleBandsLayer_members[] = {
    {NULL}  /* Sentinel */
};

/*******************************************
***  Make Polymorphism work for methods  ***
********************************************/

int numSimpleBandsMethods = 0;
static int numBandsMethods = numLayerMethods + numSimpleBandsMethods + 1;
static PyMethodDef* SimpleBandsLayer_methods = new PyMethodDef[numBandsMethods];

struct SimpleBandsMethodInitializer
{
    SimpleBandsMethodInitializer()
    {					
		for(int i = 0; i < numLayerMethods; i++)
			SimpleBandsLayer_methods[numSimpleBandsMethods + i] = Layer_methods[i];

		SimpleBandsLayer_methods[numBandsMethods - 1] = {NULL}; /* Sentinel */
    }
};

SimpleBandsMethodInitializer simpleBandsInitializer;

/*****************************************************
***  Make Polymorphism work for getters / setters  ***
******************************************************/

int numSimpleBandsGetSetters = 0;
static int numBandsGetSetters = numSimpleBandsGetSetters + numLayerGetSetters + 1;
static PyGetSetDef* SimpleBandsLayer_getseters = new PyGetSetDef[numBandsGetSetters];

struct SimpleBandsGetSetInitializer
{
    SimpleBandsGetSetInitializer()
    {
		/*SimpleBandsLayer_methods[0] = {"test", (PyCFunction)SimpleBandsLayer_test, METH_NOARGS,
									"Return the opacity of the layer"
									};*/
									
		for(int i = 0; i < numLayerGetSetters; i++)
			SimpleBandsLayer_getseters[numSimpleBandsGetSetters + i] = Layer_getseters[i];

		SimpleBandsLayer_getseters[numBandsGetSetters - 1] = {NULL}; /* Sentinel */
    }
};

SimpleBandsGetSetInitializer simpleBandsGetSetInitializer;

static PyTypeObject SimpleBandsLayerType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.SimpleBandsLayer",           	/* tp_name */
    sizeof(SimpleBandsLayerData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)SimpleBandsLayer_dealloc,  /* tp_dealloc */
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
    (reprfunc)SimpleBandsLayer_toString, /* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "SimpleBandsLayer",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    SimpleBandsLayer_methods,             	/* tp_methods */
    SimpleBandsLayer_members,				/* tp_members */
    SimpleBandsLayer_getseters,                         	/* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    SimpleBandsLayer_new,                 	/* tp_new */
};