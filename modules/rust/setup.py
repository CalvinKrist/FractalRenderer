from setuptools import setup
from setuptools_rust import Binding, RustExtension

setup(
    name="fractal",
    version="1.0",
    rust_extensions=[RustExtension("fractal.fractal", binding=Binding.PyO3)],
    packages=["fractal"],
    # rust extensions are not zip safe, just like C-extensions.
    zip_safe=False,
)