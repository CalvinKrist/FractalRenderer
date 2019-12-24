use pyo3::prelude::*;
use pyo3::wrap_pyfunction;

#[pyfunction]
/// Formats the sum of two numbers as string
fn render(a: usize, b: usize) -> PyResult<String> {
    Ok((a + b).to_string())
}

/// This module is a python module implemented in Rust.
#[pymodule]
fn fractal(_py: Python, m: &PyModule) -> PyResult<()> {
    m.add_wrapped(wrap_pyfunction!(render))?;
    
    Ok(())
}