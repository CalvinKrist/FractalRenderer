#![allow(non_snake_case)]
#![allow(dead_code)]
use pyo3::prelude::*;

mod fractal;

/// This module is a python module implemented in Rust.
/// Use Cargo Build then python ./setup.py develop to build and link
/// use help(fractal) in python to see availible methods and documentation
/// TODO Document py03 setup
#[pymodule]
fn fractal(_py: Python, m: &PyModule) -> PyResult<()> {

    #[pyfn(m, "test")]
    /// Prints to the screen and returns a test string
    fn test() -> PyResult<String> {
        let result: String =  "Yeet".to_string();
        println!("I am a test");
        Ok(result)
    }

    #[pyfn(m, "Fractal")]
    /// Creates a default fractal
    fn Fractal() -> PyResult<fractal::Fractal> {
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
        Ok(f)
    }
    
    #[pyfn(m, "HistogramLayer")]
    /// Creates a default histogram layer
    fn HistogramLayer() -> PyResult<fractal::HistogramLayer> {
        let l = fractal::HistogramLayer {
            opacity: 1.0,
            palette: fractal::Palette {
                color_list: vec!(),
                opacity_list: vec!(),
                interior_color: fractal::Color {
                    r: 0,
                    g: 0,
                    b: 0,
                },
            },
            visible: true,
            name: "default".to_string(),
        };
        Ok(l)
    }

    Ok(())
}