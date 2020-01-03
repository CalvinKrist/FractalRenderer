#![allow(non_snake_case)]
use pyo3::prelude::*;
use pyo3::wrap_pyfunction;

mod fractal;

#[pyfunction]
/// Test function prints to screen and returns yeet
fn test() -> PyResult<String> {
    let result: String =  "Yeet".to_string();
    println!("I am a test");
    Ok(result)
}

#[pyfunction]
fn Fractal() -> fractal::Fractal {
    let f = fractal::Fractal {
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


/// This module is a python module implemented in Rust.
/// Use Cargo Build then python ./setup.py develop to build and link
/// use help(fractal) in python to see availible methods and documentation
/// TODO Document py03 setup
#[pymodule]
fn fractal(_py: Python, m: &PyModule) -> PyResult<()> {
    m.add_wrapped(wrap_pyfunction!(test))?;
    m.add_wrapped(wrap_pyfunction!(Fractal))?;
    Ok(())
}