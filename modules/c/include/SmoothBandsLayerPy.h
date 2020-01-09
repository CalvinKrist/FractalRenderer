#pragma once

#include <Python.h>
#include "structmember.h"
#include "SmoothBandsLayer.h"
#include "LayerPy.h"

// Python wrapper around a C++ Layer class
typedef struct {
    PyObject_HEAD
	Layer * myLayer;
	//ParameterData * parameterData;
} SmoothBandsLayerData;

static PyObject *
SmoothBandsLayer_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	SmoothBandsLayerData *self;

    self = (SmoothBandsLayerData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myLayer = new SmoothBandsLayer();
        if (self->myLayer == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
    return (PyObject *)self;
}

static void
SmoothBandsLayer_dealloc(SmoothBandsLayerData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *  
SmoothBandsLayer_toString(SmoothBandsLayerData* self) {
	std::string description = self->myLayer->toString();
	return PyUnicode_FromFormat(description.c_str()); 
}

static PyMemberDef SmoothBandsLayer_members[] = {
    {NULL}  /* Sentinel */
};

/*******************************************
***  Make Polymorphism work for methods  ***
********************************************/

int numSmoothBandsMethods = 0;
static int numSmoothMethods = numLayerMethods + numSmoothBandsMethods + 1;
static PyMethodDef* SmoothBandsLayer_methods = new PyMethodDef[numSmoothMethods];

struct SmoothBandsMethodInitializer
{
    SmoothBandsMethodInitializer()
    {					
		for(int i = 0; i < numLayerMethods; i++)
			SmoothBandsLayer_methods[numSmoothBandsMethods + i] = Layer_methods[i];

		SmoothBandsLayer_methods[numSmoothMethods - 1] = {NULL}; /* Sentinel */
    }
};

SmoothBandsMethodInitializer smoothInitializer;

/*****************************************************
***  Make Polymorphism work for getters / setters  ***
******************************************************/

int numSmoothBandsGetSetters = 0;
static int numSmoothGetSetters = numSmoothBandsGetSetters + numLayerGetSetters + 1;
static PyGetSetDef* SmoothBandsLayer_getseters = new PyGetSetDef[numSmoothGetSetters];

struct SmoothBandsGetSetInitializer
{
    SmoothBandsGetSetInitializer()
    {
		/*SmoothBandsLayer_methods[0] = {"test", (PyCFunction)SmoothBandsLayer_test, METH_NOARGS,
									"Return the opacity of the layer"
									};*/
									
		for(int i = 0; i < numLayerGetSetters; i++)
			SmoothBandsLayer_getseters[numSmoothBandsGetSetters + i] = Layer_getseters[i];

		SmoothBandsLayer_getseters[numSmoothGetSetters - 1] = {NULL}; /* Sentinel */
    }
};

SmoothBandsGetSetInitializer smoothBandsGetSetInitializer;

static PyTypeObject SmoothBandsLayerType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.SmoothBandsLayer",           	/* tp_name */
    sizeof(SmoothBandsLayerData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)SmoothBandsLayer_dealloc,  /* tp_dealloc */
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
    (reprfunc)SmoothBandsLayer_toString, /* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "SmoothBandsLayer",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    SmoothBandsLayer_methods,             	/* tp_methods */
    SmoothBandsLayer_members,				/* tp_members */
    SmoothBandsLayer_getseters,                         	/* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    SmoothBandsLayer_new,                 	/* tp_new */
};