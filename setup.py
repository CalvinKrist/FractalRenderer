'''from distutils.core import setup, Extension
import os

# Documentation here:
# https://docs.python.org/3/extending/building.html
module = Extension('fractal',
                    define_macros = [('MAJOR_VERSION', '1'),
                                     ('MINOR_VERSION', '0')],
                    include_dirs = ['fractals/include'],
                    sources = ['fractals/src/Main.cpp'])

def main():

    cFiles = os.listdir("fractals/src")
    cFiles = ["fractals/src/" + fileName for fileName in cFiles]
    module.sources = cFiles

    setup(name="fractal",
          version="1.0.0",
          description="Python interface for the  C library function",
          author="<your name>",
          author_email="your_email@gmail.com",
          ext_modules=[module])

if __name__ == "__main__":
    main()'''

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