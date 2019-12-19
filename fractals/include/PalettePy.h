#pragma once

#include <Python.h>
#include "structmember.h"
#include "Palette.h"

// Python wrapper around a C++ Palette class
typedef struct {
    PyObject_HEAD
	Palette * myPalette;
	//ParameterData * parameterData;
} PaletteData;

static PyObject *
Palette_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	PaletteData *self;

    self = (PaletteData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myPalette = new Palette();
        if (self->myPalette == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
	/*self->parameterData = (ParameterData *)ParametersType.tp_alloc(&ParametersType, 0);
	if (self->parameterData == NULL) {
		Py_DECREF(self);
		return NULL;
	}
	
	self->parameterData->myParameters = &(self->myPalette->getParameters());*/
	
    return (PyObject *)self;
}

static void
Palette_dealloc(PaletteData * self)
{
	//Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *
Palette_colorAt(PaletteData* self, PyObject * pyX) {
	if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "colorAt: self is null");
        return NULL;
    }
	
	// Extract argument
	double x = PyFloat_AsDouble(pyX);
	
	Color color = self->myPalette->colorAt(x);
	return Py_BuildValue("iii", color.r, color.g, color.b);
}

static PyObject *
Palette_opacityAt(PaletteData* self, PyObject * pyX) {
	if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "opacityAt: self is null");
        return NULL;
    }
	
	// Extract argument
	double x = PyFloat_AsDouble(pyX);

	return PyFloat_FromDouble(self->myPalette->opacityAt(x));
}
	
	
static PyObject *
Palette_addColor(PaletteData* self, PyObject * args) {
	int r, g, b;
	double x;
		
	PyObject * colorTuple;
	
	if (!PyArg_ParseTuple(args, "Od", &colorTuple, &x)) {
        PyErr_SetString(PyExc_AttributeError, "addColor: failed to seperate color and x");
        return NULL;
    }
	
	if (!PyArg_ParseTuple(colorTuple, "iii", &r, &g, &b)) {
        PyErr_SetString(PyExc_AttributeError, "addColor: failed to parse color");
        return NULL;
    }
	
	return Py_True;
}

static PyObject *
Palette_removeColor(PaletteData* self, PyObject * pyX) {
	if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "removeColor: self is null");
        return NULL;
    }
	
	// Extract argument
	double x = PyFloat_AsDouble(pyX);
	
	return Py_True;
}

static PyObject *
Palette_addOpacity(PaletteData* self, PyObject * args) {
	double opacity, x;
		
	if (!PyArg_ParseTuple(args, "dd", &opacity, &x)) {
        PyErr_SetString(PyExc_AttributeError, "addOpacity: failed to extract args");
        return NULL;
    }
	
	return Py_True;
}

static PyObject *
Palette_removeOpacity(PaletteData* self, PyObject * pyX) {
	if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "removeOpacity: self is null");
        return NULL;
    }
	
	// Extract argument
	double x = PyFloat_AsDouble(pyX);
	
	return Py_True;
}

/**************************
**  Getters and Setters  **
***************************/

static PyObject *
Palette_getInteriorColor(PaletteData* self)
{
    if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myPalette");
        return NULL;
    }

	Color interior = self->myPalette->getInteriorColor();
    return Py_BuildValue("iii", interior.r, interior.g, interior.b);
}

static PyObject *
Palette_setInteriorColor(PaletteData *self, PyObject * pyColor)
{
    if (self->myPalette == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myPalette");
        return NULL;
    }
	
	int* interior = new int[3];
	if (!PyArg_ParseTuple(pyColor, "iii", &interior[0], &interior[1], &interior[2])) {
        PyErr_SetString(PyExc_AttributeError, "setInteriorColor: failed to extract args");
        return NULL;
    }
	
	self->myPalette->setInteriorColor(Color(interior[0], interior[1], interior[2]));

    return 0;
}

static PyObject *  
Palette_toString(PaletteData* self) {
	std::string description = self->myPalette->toString();
	return PyUnicode_FromFormat(description.c_str()); 
}

static PyGetSetDef Palette_getseters[] = {
    {"interior_color",
     (getter)Palette_getInteriorColor, (setter)Palette_setInteriorColor,
     "Palette interior color as a tuple of three integers from 0-255",
     NULL},
    {NULL}  /* Sentinel */
};

static PyMemberDef Palette_members[] = {
    /*{"parameters", T_OBJECT, offsetof(PaletteData, parameterData), 0,
     "The Palette's parameters"},*/
    {NULL}  /* Sentinel */
};

static PyMethodDef Palette_methods[] = {
	{"color_at", (PyCFunction)Palette_colorAt, METH_O,
    "Returns the color at a point in the palette ranging from 0 to 1"},
	{"opacity_at", (PyCFunction)Palette_opacityAt, METH_O,
    "Returns the opacity at a point in the palette ranging from 0 to 1"},
	{"add_color", (PyCFunction)Palette_addColor, METH_VARARGS,
    "Adds a color at a point in the palette ranging from 0 to 1. Returns success value."},
	{"remove_color", (PyCFunction)Palette_removeColor, METH_O,
    "Removes a color at a point in the palette ranging from 0 to 1. Returns success value."},
	{"add_opacity", (PyCFunction)Palette_addOpacity, METH_VARARGS,
    "Adds an opacity at a point in the palette ranging from 0 to 1. Returns success value."},
	{"remove_opacity", (PyCFunction)Palette_removeOpacity, METH_O,
    "Removes an opacity at a point in the palette ranging from 0 to 1. Returns success value."},
    {NULL}  /* Sentinel */
};

static PyTypeObject PaletteType {
    PyVarObject_HEAD_INIT(NULL, 0)
    "fractal.Palette",           	/* tp_name */
    sizeof(PaletteData), 			/* tp_basicsize */
    0,                         	/* tp_itemsize */
    (destructor)Palette_dealloc,  /* tp_dealloc */
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
    (reprfunc)Palette_toString, /* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "Abstract Palette superclass",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    Palette_methods,             	/* tp_methods */
    Palette_members,				/* tp_members */
    Palette_getseters,            /* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    Palette_new,                 	/* tp_new */
};