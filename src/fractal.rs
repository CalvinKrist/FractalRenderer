use pyo3::prelude::*;

#[derive(Debug)]
pub struct Color {
    r: u8,
    g: u8,
    b: u8,
}

#[derive(Debug)]
pub struct OpacityPoint {
    opacity: f64,
    location: f64,
}

#[derive(Debug)]
pub struct ColorPoint {
    color: Color,
    location: f64,
}

#[derive(Debug)]
pub struct Palette {
    color_list: Vec<ColorPoint>,
    opacity_list: Vec<OpacityPoint>,
    interior_color: Color,
}

#[derive(Debug)]
pub struct Layer {
    opacity: f32,
    palette: Palette,
    visible: bool,
    name: String,
}

#[pyclass]
#[derive(Debug)]
pub struct Fractal {
    pub x: f64,
    pub y: f64,
    pub viewport_width: f64,
    pub width: i32,
    pub height: i32,
    pub layers: Vec<Layer>,
    pub image: Vec<u8>,
    pub layer_image: Vec<u8>,
}