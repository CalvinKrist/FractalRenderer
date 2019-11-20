#pragma once

#include <Python.h>
#include "structmember.h"
#include "Fractal.h"

#include <iostream>

// Python wrapper around a C++ Fractal class
typedef struct {
    PyObject_HEAD
	Fractal * myFractal;
	//ParameterData * parameterData;
} FractalData;

static PyObject *
Fractal_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	FractalData *self;

    self = (FractalData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myFractal = new Fractal();
        if (self->myFractal == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
	/*self->parameterData = (ParameterData *)ParametersType.tp_alloc(&ParametersType, 0);
	if (self->parameterData == NULL) {
		Py_DECREF(self);
		return NULL;
	}
	
	self->parameterData->myParameters = &(self->myFractal->getParameters());*/
	
    return (PyObject *)self;
}

static void
Fractal_dealloc(FractalData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *
Fractal_render(FractalData* self, PyObject *args)
{
    if (self->myFractal == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myFractal");
        return NULL;
    }

	std::cout << self->myFractal->getX() << std::endl;
	
    return PyFloat_FromDouble(1);
}

/**************************
**  Getters and Setters  **
***************************/
static PyObject *
Fractal_getX(FractalData* self, void *closure)
{	
    return PyFloat_FromDouble(self->myFractal->getX());
}

static int
Fractal_setX(FractalData* self, PyObject *value, void *closure)
{	
	if (self->myFractal == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myFractal");
        return NULL;
    }
	
	double newX = PyFloat_AsDouble(value);
	self->myFractal->setX(newX);
	
    return 0;
}

static PyObject *
Fractal_getY(FractalData* self, void *closure)
{	
    return PyFloat_FromDouble(self->myFractal->getY());
}

static int
Fractal_setY(FractalData* self, PyObject *value, void *closure)
{	
	if (self->myFractal == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myFractal");
        return NULL;
    }
	
	double newY = PyFloat_AsDouble(value);
	self->myFractal->setY(newY);
	
    return 0;
}

static PyObject *
Fractal_getViewportWidth(FractalData* self, void *closure)
{	
    return PyFloat_FromDouble(self->myFractal->getViewportWidth());
}

static int
Fractal_setViewportWidth(FractalData* self, PyObject *value, void *closure)
{	
	if (self->myFractal == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myFractal");
        return NULL;
    }
	
	double newViewWidth = PyFloat_AsDouble(value);
	self->myFractal->setViewportWidth(newViewWidth);
	
    return 0;
}

static PyGetSetDef Fractal_getseters[] = {
    {"x",
     (getter)Fractal_getX, (setter)Fractal_setX,
     "viewport x value as a real number",
     NULL},
	{"y",
     (getter)Fractal_getY, (setter)Fractal_setY,
     "viewport y value as a real number",
     NULL},
	{"viewport_width",
     (getter)Fractal_getViewportWidth, (setter)Fractal_setViewportWidth,
     "viewport width as a rel number",
     NULL},
    {NULL}  /* Sentinel */
};

static PyMemberDef Fractal_members[] = {
    /*{"parameters", T_OBJECT, offsetof(FractalData, parameterData), 0,
     "The Fractal's parameters"},*/
    {NULL}  /* Sentinel */
};

static PyMethodDef Fractal_methods[] = {
    {"render", (PyCFunction)Fractal_render, METH_VARARGS,
    "Renders the fractal and returns a 2D color array"},
    {NULL}  /* Sentinel */
};


static PyTypeObject FractalType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.Fractal",           	/* tp_name */
    sizeof(FractalData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)Fractal_dealloc,  /* tp_dealloc */
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
    "Fractal Fractal test",     /* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    Fractal_methods,            /* tp_methods */
    Fractal_members,			/* tp_members */
    Fractal_getseters,          /* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    Fractal_new,                 	/* tp_new */
};