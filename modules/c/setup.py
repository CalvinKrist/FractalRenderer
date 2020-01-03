from distutils.core import setup, Extension
import os

# Documentation here:
# https://docs.python.org/3/extending/building.html
module = Extension('fractal',
                    define_macros = [('MAJOR_VERSION', '1'),
                                     ('MINOR_VERSION', '0')],
                    include_dirs = ['include'],
                    sources = ['src/Main.cpp'])

def main():

    cFiles = os.listdir("src")
    cFiles = ["src/" + fileName for fileName in cFiles]
    module.sources = cFiles

    setup(name="fractal",
          version="0.1.0",
          description="Python interface for C fractal libraries",
          author="Calvin Krist",
          author_email="calvin.krist@yahoo.com",
          ext_modules=[module])

if __name__ == "__main__":
    main()