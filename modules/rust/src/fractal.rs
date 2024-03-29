use pyo3::prelude::*;

#[derive(Debug)]
pub struct Color {
    pub r: u8,
    pub g: u8,
    pub b: u8,
}

#[derive(Debug)]
pub struct OpacityPoint {
    pub opacity: f64,
    pub location: f64,
}

#[derive(Debug)]
pub struct ColorPoint {
    pub color: Color,
    pub location: f64,
}

#[derive(Debug)]
pub struct Palette {
    pub color_list: Vec<ColorPoint>,
    pub opacity_list: Vec<OpacityPoint>,
    pub interior_color: Color,
}


/// Abstraction for a layer
pub trait Layer {
    fn render(&self) -> Vec<u8>;
}


#[derive(Debug)]
pub struct HistogramLayer {
    pub opacity: f32,
    pub palette: Palette,
    pub visible: bool,
    pub name: String,
}

impl Layer for HistogramLayer {
    fn render(&self) -> Vec<u8> {
        vec!()
    }
}

#[pyclass]
pub struct Fractal {
    pub x: f64,
    pub y: f64,
    pub viewport_width: f64,
    pub width: i32,
    pub height: i32,
    pub layers: Vec<Box<dyn Layer>>,
    pub image: Vec<u8>,
    pub layer_image: Vec<u8>,
}