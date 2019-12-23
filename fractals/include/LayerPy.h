#pragma once

#include <Python.h>
#include "structmember.h"
#include "Layer.h"
#include "HistogramLayer.h"
#include "PalettePy.h"

// Python wrapper around a C++ Layer class
typedef struct {
    PyObject_HEAD
	Layer * myLayer;
	//ParameterData * parameterData;
} LayerData;

static PyObject *
Layer_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	LayerData *self;

    self = (LayerData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myLayer = new HistogramLayer();
        if (self->myLayer == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
	/*self->parameterData = (ParameterData *)ParametersType.tp_alloc(&ParametersType, 0);
	if (self->parameterData == NULL) {
		Py_DECREF(self);
		return NULL;
	}
	
	self->parameterData->myParameters = &(self->myLayer->getParameters());*/
	
    return (PyObject *)self;
}

static void
Layer_dealloc(LayerData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

/**************************
**  Getters and Setters  **
***************************/

static PyObject *
Layer_getOpacity(LayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    return PyFloat_FromDouble(self->myLayer->getOpacity());
}

static PyObject *
Layer_setOpacity(LayerData *self, PyObject * pyOpacity)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	float newOpacity = (float)PyFloat_AsDouble(pyOpacity);
	self->myLayer->setOpacity(newOpacity);

    return 0;
}

static PyObject *
Layer_getName(LayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    return PyUnicode_FromFormat(self->myLayer->getName().c_str()); 
}

static PyObject *
Layer_setName(LayerData *self, PyObject * pyString)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	const char* newName = PyUnicode_AsUTF8(pyString);
	self->myLayer->setName(std::string(newName));

    return 0;
}

static PyObject *
Layer_getIsVisible(LayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    if(self->myLayer->isVisible())
		return Py_True;
	return Py_False;
}

static PyObject *
Layer_setIsVisible(LayerData *self, PyObject * pyString)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	if(pyString == Py_True)
		self->myLayer->setVisible(true);
	else
		self->myLayer->setVisible(false);
		
    return 0;
}

static PyObject *
Layer_getPalette(LayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    // TODO: potential memory leak
	PaletteData * pPaletteData = (PaletteData *)PaletteType.tp_alloc(&PaletteType, 0);
	pPaletteData->myPalette = &(self->myLayer->getPalette());
	Py_INCREF(pPaletteData);
    return (PyObject*)pPaletteData;
}

static PyObject *
Layer_setPalette(LayerData *self, PyObject * arg)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	PaletteData * pyPalette = (PaletteData*)arg;
	self->myLayer->setPalette(*(pyPalette->myPalette));
		
    return 0;
}

static PyObject *  
Layer_toString(LayerData* self) {
	std::string description = self->myLayer->toString();
	return PyUnicode_FromFormat(description.c_str()); 
}

static PyGetSetDef Layer_getseters[] = {
    {"opacity",
    (getter)Layer_getOpacity, (setter)Layer_setOpacity,
    "Layer opacity as a float",
    NULL},
	{"name",
    (getter)Layer_getName, (setter)Layer_setName,
    "Layer name as UTF-8 string",
    NULL},
	{"is_visible",
    (getter)Layer_getIsVisible, (setter)Layer_setIsVisible,
    "Layer visibility as a boolean",
    NULL},
	{"palette",
    (getter)Layer_getPalette, (setter)Layer_setPalette,
    "Layer palette object",
    NULL},
    {NULL}  /* Sentinel */
};

static PyMemberDef Layer_members[] = {
    /*{"parameters", T_OBJECT, offsetof(LayerData, parameterData), 0,
     "The layer's parameters"},*/
    {NULL}  /* Sentinel */
};

static PyMethodDef Layer_methods[] = {
    {NULL}  /* Sentinel */
};

static PyTypeObject LayerType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.Layer",           	/* tp_name */
    sizeof(LayerData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)Layer_dealloc,  /* tp_dealloc */
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
    (reprfunc)Layer_toString, /* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "Abstract layer superclass",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    Layer_methods,             	/* tp_methods */
    Layer_members,				/* tp_members */
    Layer_getseters,            /* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    Layer_new,                 	/* tp_new */
};