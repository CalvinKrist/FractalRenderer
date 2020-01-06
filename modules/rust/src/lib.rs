#![allow(non_snake_case)]
#![allow(dead_code)]
use pyo3::prelude::*;
use pyo3::wrap_pyfunction;

#[pyfunction]
/// Prints to the screen and returns a test string
fn test() -> PyResult<String> {
    let result: String = "Yeet".to_string();
    println!("I am a test");
    Ok(result)
}

#[pyfunction]
/// Creates a default fractal
fn Fractal() -> Fractal {
    let f = Fractal {
            x: 0.0,
            y: 0.0,
            viewport_width: 4.0,
            width: 600,
            height: 400,
            layers: vec!(),
            image: vec!(),
            layer_image: vec!()
        };
    return f;
}

#[pyfunction]
/// Creates a default histogram layer
fn HistogramLayer() -> HistogramLayer {
    let l = HistogramLayer {
        opacity: 1.0,
        palette: Palette {
            color_list: vec!(),
            opacity_list: vec!(),
            interior_color: Color {
                r: 0,
                g: 0,
                b: 0,
            },
        },
        visible: true,
        name: "default".to_string(),
    };
    return l;
}

/// This module is a python module implemented in Rust.
/// Use Cargo Build then python ./setup.py develop to build and link
/// use help(fractal) in python to see availible methods and documentation
/// TODO Document py03 setup
#[pymodule]
fn fractal(_py: Python, m: &PyModule) -> PyResult<()> {
    m.add_wrapped(wrap_pyfunction!(test))?;
    m.add_wrapped(wrap_pyfunction!(HistogramLayer))?;
    m.add_class::<Fractal>()?;
    Ok(())
}

#[pyclass]
#[derive(Debug)]
pub struct Color {
    pub r: u8,
    pub g: u8,
    pub b: u8,
}

#[pyclass]
#[derive(Debug)]
pub struct OpacityPoint {
    pub opacity: f64,
    pub location: f64,
}

#[pyclass]
#[derive(Debug)]
pub struct ColorPoint {
    pub color: Color,
    pub location: f64,
}

#[pyclass]
#[derive(Debug)]
pub struct Palette {
    pub color_list: Vec<ColorPoint>,
    pub opacity_list: Vec<OpacityPoint>,
    pub interior_color: Color,
}

/// Abstraction for a layer
pub enum Layer {
    HistogramLayer
}

#[pyclass]
#[derive(Debug)]
pub struct HistogramLayer {
    pub opacity: f32,
    pub palette: Palette,
    pub visible: bool,
    pub name: String,
}

#[pyclass(module = "fractal")]
pub struct Fractal {
    #[pyo3(get, set)]
    pub x: f64,
    #[pyo3(get, set)]
    pub y: f64,
    #[pyo3(get, set)]
    pub viewport_width: f64,
    #[pyo3(get, set)]
    pub width: i32,
    #[pyo3(get, set)]
    pub height: i32,
    pub layers: Vec<Layer>,
    #[pyo3(get, set)]
    pub image: Vec<u8>,
    #[pyo3(get, set)]
    pub layer_image: Vec<u8>,
}

#[pymethods]
impl Fractal {

    #[new]
    fn new(obj: &PyRawObject) { 
        obj.init(Fractal {
            x: 0.0,
            y: 0.0,
            viewport_width: 4.0,
            width: 600,
            height: 400,
            layers: vec!(),
            image: vec!(),
            layer_image: vec!()
        });
    }

    fn yeet(&self, _py: Python<'_>,) -> PyResult<String> {
        Ok("Yeet".to_string())
    }

}