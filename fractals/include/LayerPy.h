#ifndef LayerPy_HEADER
#define LayerPy_HEADER

#include <Python.h>
#include "structmember.h"
#include "Layer.h"
#include "ParametersPy.h"

// Python wrapper around a C++ Layer class
typedef struct {
    PyObject_HEAD
	Layer * myLayer;
	ParameterData * parameterData;
} LayerData;

static PyObject *
Layer_new (PyTypeObject *type, PyObject *args, PyObject *kwds) {
	LayerData *self;

    self = (LayerData *)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->myLayer = new Layer();
        if (self->myLayer == NULL) {
            Py_DECREF(self);
            return NULL;
        }
    }
	
	self->parameterData = (ParameterData *)ParametersType.tp_alloc(&ParametersType, 0);
	if (self->parameterData == NULL) {
		Py_DECREF(self);
		return NULL;
	}
	
	self->parameterData->myParameters = &(self->myLayer->getParameters());
	
    return (PyObject *)self;
}

static void
Layer_dealloc(LayerData * self)
{
	Py_XDECREF(self->parameterData);
    Py_TYPE(self)->tp_free(self);
}

static PyObject *
Layer_get_opacity(LayerData* self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }

    return PyFloat_FromDouble(self->myLayer->getOpacity());
}

static PyObject *
Layer_set_opacity(LayerData *self, PyObject * pyOpacity)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	float newOpacity = (float)PyFloat_AsDouble(pyOpacity);
	self->myLayer->setOpacity(newOpacity);

    return Py_BuildValue("");
}

static PyObject *
Layer_get_parameters(LayerData *self)
{
    if (self->myLayer == NULL) {
        PyErr_SetString(PyExc_AttributeError, "myLayer");
        return NULL;
    }
	
	ParameterData * pParamData = self->parameterData;
	pParamData->myParameters->test();
	
	Py_INCREF(pParamData);
	
    return (PyObject*)pParamData;
}

static PyMemberDef Layer_members[] = {
    {"parameters", T_OBJECT, offsetof(LayerData, parameterData), 0,
     "The layer's parameters"},
    {NULL}  /* Sentinel */
};

static PyMethodDef Layer_methods[] = {
    {"get_opacity", (PyCFunction)Layer_get_opacity, METH_NOARGS,
     "Return the opacity of the layer"
    },
	{"set_opacity", (PyCFunction)Layer_set_opacity, METH_O,
     "Set the opacity of the layer"
    },
	{"get_parameters", (PyCFunction)Layer_get_parameters, METH_NOARGS,
     "Return the parameters of the layer"
    },
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
    0,            				/* tp_str */
    0,                         	/* tp_getattro */
    0,                         	/* tp_setattro */
    0,                         	/* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,        	/* tp_flags */
    "Fractal layer test",     	/* tp_doc */
	0,                         	/* tp_traverse */
    0,                         	/* tp_clear */
    0,                         	/* tp_richcompare */
    0,                         	/* tp_weaklistoffset */
    0,                         	/* tp_iter */
    0,                         	/* tp_iternext */
    Layer_methods,             	/* tp_methods */
    Layer_members,				/* tp_members */
    0,                         	/* tp_getset */
    0,                         	/* tp_base */
    0,                         	/* tp_dict */
    0,                         	/* tp_descr_get */
    0,                         	/* tp_descr_set */
    0,                         	/* tp_dictoffset */
    0,      					/* tp_init */
    0,                         	/* tp_alloc */
    Layer_new,                 	/* tp_new */
};

#endif