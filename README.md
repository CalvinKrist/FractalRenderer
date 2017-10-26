# DistributedFractalNetwork
### Fractal Editing and Distributed Rendering
By Calvin Krist and David Smith

### Program  Requirements:

··* At least Java 8.

··* A jdk if one wants to use custom rendering layers. Otherwise, a jre will do.

··* A decent computer (4gb ram recomended, intel cpu gen 5+).


### How to install:

1. Place the .jar in a new folder along with the textures folder. Launch the jar.

2. The jar will create all the folders it needs within the folder you placed it. Do not delete these. The program is now ready for use.


### Navigating the fractal:

··*click around on the fractal to move the viewport.

··*Click ',' to zoom in and '.' to zoom out.


### Editing the fractal:

··* The fractal consists of a series of layers that are rendered in a certain order. There are different types of layers and each can have a different gradient.

··* To add a new layer, double click the '+' button.

··* To edit the type of layer, the name of the layer, and the opacity of the layer, double click the layer.

··* To change the order in which layers are rendered, use the the left and the right arrow keys while selecting a fractal.

··* When a layer is selected, its gradient will be displayed below the fractal. Arrow buttons on the bottom of the gradient represent color data points, and arrow buttons on top of the gradient represent opacity data points.

··* New points can be added by clicking above or below the gradient. The data of each of the points can be edited by clicking the points.

··* The colored, square rectangle to the right of the gradient is the color the inside of the fractal will be colord. It can be modified by clicking the square.

··* The two buttons below the square represent ways to save and load in palettes. By default, palettes are saved as a part of the fractal, but if you create one that you especially love and wish to use later save it to the palettes folder. You can then load it in to other fractals!

··* When a layer is selected, layer-based parameters that can be edited will show up in the upper right hand corner of the screen. Double click those parameters to edit them.	


### Menus:

··* 'New Fractal', under the 'Frctal' menu, will open up a new, default fractal. All changes made to the previous fractal will be lost, so save first.

··* 'Open Fractal', under the 'Fractal' menu, will open a dialog alowing the user to open up a previously saved fractal. Opening this fractal will result in all changes to the old one being lost.

··* 'Save Fractal', under the 'Fractal' menu, will save the fractal. If the fractal has never been saved before, it will open a dialog alowing the user to name the fracral and choose a file location. Otherwise, it will save to the file location.

··* 'Save Fractal As', under the 'Fractal' menu, will save the fractal as a new file. It will not change the name of file location of the fractal. This is useful for version control.

··* 'Export Fractal', under the 'Fractal' menu, will alow the user to export the fractal as a .png or a .jpg image to a specified file location. The user can also choose a resolution for the saved image.

··* 'Create New Network', under the 'Network' menu, opens up a series of dialogs helping the user set up a network for use in a distributed zoom. The user chooses a fractal to zoom in on and sets parameters. The user can only choose from fractals stored in the 'fractals' folder.

··* 'View Network' will, under the 'Network' menu, will, if a network has been started, alow the user to visualy view the network. It will display statistics about the network, alow the user to modify the parameters, and give the user some basic control over all computers connected to the distributed network. There are more menus at the top of the Network View as well.

··* 'View Network Log', under the 'Network' menu, alows the user to view the log of the network, assuming a network has been started. The Network log can also be viewed through the 'Network View'.

··* 'Close Network', under the 'Network' menu, will shut down the network.

··* 'New Layer Type', under the 'System' menu, will alow the user to register a new layer type while the application is still running. The layer could also be registered by placing it in the 'custom' folder and restarting the application.

··* 'Edit Log Options', under the 'System' menu, will alow the user to modify the log options. There are two main categories: log and print. Log controls what is saved to the log file, while print controls what is printed to the cmd (if it is used to run the application). LEVEL_ERROR prints only errors, LEVEL_LOg prints everything, and LEVEL_NONE prints nothing.


### Running a Network:

1. First, there must be a fractal file saved in the 'fractals' folder. You will be able to choose between all fractals in that folder for the network to zoom in on

2. Next, follow the menus that will help set up the network by clicking the 'Create New Network' menu option. After that is up and running, you can view the network by clicking 'View Network'. You can also view the network log either through the fractal editor menus or through the Network View menus.

3. To connect clients to the network, run the 'Client' application. The clients must be on the local network. If there are issues, ensure that ports 6664 and 8888 are open and that your router allows for UDP broadcasts. 

4. The images rendered by the clients will save to the directory selected during the Network creation process. You can compile those images into a video however you want.

5. You can close the network when you are done by either using the 'Close Network' option in the menus or by closing the fractal editor. The client computers do not need to only render the fractals: feel free to use them while they render. **NOTE: DOES NOT WORK PROPERLY. NETWORK MUST BE CLOSED BY RESTARTING THE APPLICATION.**

6. If there is an issue with the client and it crashes, it will save a log file in the directory that the jar is placed in.

7. If your clients are having issues connecting to the network, make sure the firewall has an exception over port 6664 and that you are trying to connect to the IPv4 address of the server computer.	


### Creating new layer types:

··* If you wish to create and use your own, custom layer types, you are free to do so. There are two ways to add them to the program. 

··* The easiest is to place the .java file in the 'custom' folder or, if you have a .class file, place that in the 'custom/fractal' folder. Upon startup, any layer files located in these folders will be added to the registry and made usable.

··* Alternatively, if you have the .java file, you can use the 'New Layer Type' menu to import it into the application without restarting it. This layer type will be removed if the application is retarted unless the .java file is in the 'custom' folder.

··* IMPORTANT: Adding custom layers to the application is done through dynamic rendering with the Reflections API. This will ONLY work if the application is run through a jdk. If it is run with a jre, your custm layers will not be added.


### Developer Information:

··* The Mandelbrot Explorer is well documented in the included Javadocs (see 'doc' folder). 

··* For information on how to implement a custom rendering method and add it to this application, see the below tutorial. For more information, see the Javadocs for the fractal/Layer class, the fractal/HistogramRenderer class, and the fractal/TriangleAverageLayer class.


### Custom Rendering Tutorial:

To begin, create a new Java class and call it `MyLayer`. Make sure it extends the `Layer` class from fractal/Layer. Next, add empty methods for all the unimplemented methods from the `Layer` class.

    public class MyLayer extends Layer {
    
        @Override
        protected void render(Color[][]pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {

        }

        @Override
        public Parameters getParameters() {
            return null;
        }

        @Override
        public void setParameters() {

 
        }

        @Override
        protected void calculateBailout(double rWidth, double rHeight) {

        }

        @Override
        protected void calculateIterations(double rWidth, double rHeight) {

        }

    }

Alright, there's a lot in that above code. To begin, the `Layer` class defines a layer that contains code on how to render the Mandelbrot fractal. This layer can be added to the fractal editor to change the appearance of the image. This layer, furthermore, can be moved up and down the list of layers to change the rendering order or be removed entirely. Layers can also be named or have their opacity modified.

The `render` method is where the meat of a layer resides. As the name implies, this is where the rendering code is. The layer is passed parameters describing the state of the fractal as well as a 2D array of colors where the rendered image is stored. 

The `getParameters` and `setParameters` methods supports the editable layer parameters the user sees near the top right of the GUI. When the GUI needs to know which layer parameters a user can modify, it calls the `getParameters` method, and when those parameters are changed the GUI calls the `setParameters` method. The two default rendering layers support bailout and maxIterations for editable parameters, and this tutorial will implement both of those.

For both of the default rendering layers, the bailout is set to 'auto' by default. When this is the case, the method `calculateBailout` is called, and the layer will choose a bailout value to use based on the zoom level of the fractal. The same is done but for maxIterations if the corresponding parameter is set to 'auto' in the GUI.

If you still have questions about these methods, each parameter and method is well described in the Javadocs. Now, it's time to write a simple method to render the fractal! We'll start by looping through each pixel of the requested image and converting them to real unit coordinates.

    for(int i = 0; i < width; i++)
        for(int k = 0; k < height; k++) {
            #Convert pixel coordinate (i,k) to real unit coordinate (x,y)
	    double x = (i / (double)width) * rWidth * 2 - rWidth + xPos;
            double y = (k / (double)height) * rHeight * 2 - rHeight - yPos;		
    }

To continue any further, we need to dip our toes into the mathematics that make the colorful shapes of the Mandelbrot set possible. First of all, the Mandelbrot set exists in the complex number plane: the x coordinate is a real number, but the y coordinate is an imaginary number. Hence, each point in the fractal can be represented by the complex number c = x + iy, where i is the square root of -1. That complex number is then put through an iterative function where z0, the first term, equals c. The second value, z1 = z0^2 + c. The third value, z2 = z1^2 + c, and so forth. Generally, zn = (zn-1)^2 + c
For some values of c, this iterative function quickly goes to infinity. For others, this will never happen. That is why we need a 'maxIterations': once we have iterated through the function this number of times, we assume the function will never reach infinity. Otherwise, we would loop endlessly! Furthermore, there's no infinity in programming, so we need to decide a cutoff value. Mathematically, it can be shown that if the function has a magnetude of at least 2 it will eventually approach infinity, to our cutoff value--called a bailout-- can be set to 2 for now.
A simple black and white Mandelbrot set just shows which values c (which represent coordinates) go to infinity, and which do not. Let's see what it looks like.

First, let's define some values we need to calculate the set.

    double z = 0; //The real component of z
    double zi = 0; //The imaginary component of z
    int iterations = 0; //The number of times the function as iterated
    double newz; //A placeholder that we will use in the iterative function to update the z and zi values.
    
Now, we need to loop until we've reached some maximum number of iterations or the magnitude of the function has exceeded our bailout balue. The `Layer` class already has global variables for the bailout and maxIterations, so we'll use those.

    for(iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < bailout * bailout; iterations++) {

    }

Note that (z * z) + (zi * zi) is equal to the magnitude of the function squared, which is why it's compared to the bailout squared. Alright, now how do we actually calculate the value of z? Well, as some point we need to square the old value. A complex number (a + bi) squared is equal to (a^2 - b^2 +2abi). We will need to add c to that result (see the above description of the function), and then that will be our new value. Hence, the final value for the next iteration of the function is a^2 - b^2 + 2abi + x + y. In our code, a=z, b=zi, and we ignore that imaginary number by ensuring the real and imaginary components of our function are always seperated.

    newz = (z * z) - (zi * zi) + x; //the real component of the next iteration of the function
    zi = 2 * z * zi + y; //The imaginary component of the next iteration of the function.
    z = newz;

And that's it! That's all the math needed to calculate the Mandelbrot set. So how do we render it? A very simplistic approach, which is all we will do in this tutorial, is to color anything inside the fractal black and anything outside the fractal white. The points inside the fractal are those that never converged on infinity: in other words, they're the points that iterated the maxIterations number of times. Everything else is assumed to be outside the fractal.

The code for this is very simple:
    pixels[i][k] = iterations == maxIterations ? Color.black : Color.white;

At the end, the rendering method looks like this:
    for(int i = 0; i < width; i++)
        for(int k = 0; k < height; k++) {
	    double x = (i / (double)width) * rWidth * 2 - rWidth + xPos;
	    double y = (k / (double)height) * rHeight * 2 - rHeight - yPos;
				
	    double z = 0; //The real component of z
	    double zi = 0; //The imaginary component of z
	    int iterations = 0; //The number of times the function as iterated
	    double newz; //A placeholder that we will use in the iterative function to update the z and zi values.
			    
	    for (iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < bailout * bailout; iterations++) {
	        newz = (z * z) - (zi * zi) + x;
		zi = 2 * z * zi + y;
		z = newz;
	    }
	    pixels[i][k] = iterations == maxIterations ? Color.black : Color.white;
    }
And our fractal like:
IMAGE


IMAGE STUFF

Alright, now to implement the `getParameters` method. This method returns a Parameters object (look at the Javadocs for information on that Parameters object) that maps parameters to the current values. Let's allow users to change the maxIterations value. In order to do this, we'll create a Parameters object, add an entry called "maxIterations" with a default value of 100, and then return that object.

    @Override
    public Parameters getParameters() {
        Parameters props = new Parameters(new HashMap<String, Serializable>());
	props.put("maxIterations", 100);
	return props;
    }

Now we need to make sure that, when the user changes the value, we update it. In the `setParameters` method, we will get the value from the Parameters object associated to the key "maxIterations" and set that to the rendering layer's maximum iteration count.
    @Override
    public void setParameters(Parameters newProperties) {
        this.maxIterations = Integer.valueOf(newProperties.getParameter("maxIterations", String.class));
    }
Note that we pass the `getParameter` method a class: this is because the Parameters object stores Serializables, in order to ensure the data is easily sent over networks. Hence, if we just ask for the parameter we'll get a serializable. However, we can pass the String class to the method and the serializable will be cast to a String for us. From there, we can convert to the string to an integer for use in our rendering layer. At this point, if to import the layer into the rendering application and create a layer of type "MyLayer", you will see a parameter called "maxIterations" with a value of 100. If double clicked, the value can be modified: try settig it to 6 and seeing what happens!

The implementation of `calculateIterations` and `calculateBailout` is beyond the scope of this tutorial because the math required to identify those values at a given zoom level is rather advanced. For now, it is enough to set each to return 10000. Just know that, if the autoBailout or autoMaxIterations values in the Layer object are set to true, those methods will be called to calculate the correct values.

So how do we actually import `MyLayer` into the rendering application? Place MyLayer.java in the 'custom' folder in the rendering application. From there, within the rendering application go to the "System" menu and select "New Layer Type". Select MyLayer.java and click open. Now, if you double click any existing layer you will see "MyLayer" on the layer selection drop down menu! Furthermore, as long as you leave custom/fractal/MyLayer.class alone, MyLayer will always apear in that drop down menu.