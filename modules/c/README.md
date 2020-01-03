## Testing:

`python test.py`

## Python Documentation:

#### Parsing args:
https://docs.python.org/3/c-api/arg.html

#### Making objects:
https://docs.python.org/3.5/extending/newtypes.html

#### Exception Constants:
https://docs.python.org/3/c-api/exceptions.html

## Docs:

Efficiently return buffer to python:
https://stackoverflow.com/questions/28560045/best-way-to-copy-c-buffer-to-python

## Speed Test Results

The C calculations are how long it took to get a rendered result from the C modules with just a basic, single layer 'Simple Bands' type fractal.

The Python render time is how long it took to then display those results in the rendered Python GUI that is used to develop palettes and find interesting parts of the fractal.

### 12/24/2019
```
C calculations: 
Average time 800x650 for 75 trials: 0.05229166666666667
Correct: True
-----------------------------
Average time 1000x1000 for 50 trials: 0.10125
Correct: True
-----------------------------
Average time 5000x5000 for 20 trials: 2.64375
Correct: True
-----------------------------
Average time 10000x10000 for 5 trials: 16.425
Correct: True

Python Render Time
Average time rendered in PyQt5: 0.9625
```