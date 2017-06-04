# DistributedFractalNetwork
Fractal Editing and Distributed Rendering

Program  Requirements:

	-At least Java 8

	-A jdk if one wants to use custom layers. Otherwise, a jre will do

	-A decent computer (4gb ram recomended, intel cpu gen 5+)


How to install:

	-Place the .jar in a new folder. Launch the jar.

	-The jar will create all the folders it needs within the folder you placed it. Do not delete these. The program is now ready for use.


Navigating the fractal:

	-click around on the fractal to move the viewport

	-Click ',' to zoom in and '.' to zoom out


Editing the fractal:

	-The fractal consists of a series of layers that are rendered in a certain order. There are different types of layers and each can have a different gradient.

	-To add a new layer, double click the '+' button.

	-To edit the type of layer, the name of the layer, and the opacity of the layer, double click the layer.

	-To change the order in which layers are rendered, drag the layers around. Layers on top are rendered on top.

	-When a layer is selected, its gradient will be displayed below the fractal. Arrow buttons on the bottom of the gradient represent color data points, and arrow buttons on top of the gradient represent opacity data points.

	-New points can be added by clicking above or below the gradient. The data of each of the points can be edited by clicking the points.

	-The colored, square rectangle to the right of the gradient is the color the inside of the fractal will be colord. It can be modified by clicking the square.

	-The two buttons below the square represent ways to save and load in palettes. By default, palettes are saved as a part of the fractal, but if you create one that you especially love and wish to use later save it to the palettes folder. You can then load it in to other fractals!


Menus:

	-'New Fractal', under the 'Frctal' menu, will open up a new, default fractal. All changes made to the previous fractal will be lost, so save first.

	-'Open Fractal', under the 'Fractal' menu, will open a dialog alowing the user to open up a previously saved fractal. Opening this fractal will result in all changes to the old one being lost.

	-'Save Fractal', under the 'Fractal' menu, will save the fractal. If the fractal has never been saved before, it will open a dialog alowing the user to name the fracral and choose a file location. Otherwise, it will save to the file location.

	-'Save Fractal As', under the 'Fractal' menu, will save the fractal as a new file. It will not change the name of file location of the fractal. This is useful for version control.

	-'Export Fractal', under the 'Fractal' menu, will alow the user to export the fractal as a .png or a .jpg image to a specified file location. The user can also choose a resolution for the saved image.

	-'Create New Network', under the 'Network' menu, opens up a series of dialogs helping the user set up a network for use in a distributed zoom. The user chooses a fractal to zoom in on and sets parameters. The user can only choose from fractals stored in the 'fractals' folder.

	-'View Network' will, under the 'Network' menu, will, if a network has been started, alow the user to visualy view the network. It will display statistics about the network, alow the user to modify the parameters, and give the user some basic control over all computers connected to the distributed network. There are more menus at the top of the Network View as well.

	-'View Network Log', under the 'Network' menu, alows the user to view the log of the network, assuming a network has been started. The Network log can also be viewed through the 'Network View'.

	-'Close Network', under the 'Network' menu, will shut down the network.

	-'New Layer Type', under the 'System' menu, will alow the user to register a new layer type while the application is still running. The layer could also be registered by placing it in the 'custom' folder and restarting the application.

	-'Edit Log Options', under the 'System' menu, will alow the user to modify the log options. There are two main categories: log and print. Log controls what is saved to the log file, while print controls what is printed to the cmd (if it is used to run the application). LEVEL_ERROR prints only errors, LEVEL_LOg prints everything, and LEVEL_NONE prints nothing.


Running a Network:

	-First, there must be a fractal file saved in the 'fractals' folder. You will be able to choose between all fractals in that folder for the network to zoom in on

	-Next, follow the menus that will help set up the network by clicking the 'Create New Network' menu option. After that is up and running, you can view the network by clicking 'View Network'. You can also view the network log either through the fractal editor menus or through the Network View menus.

	-To connect clients to the network, run the 'Client' application. The clients must be on the local network. If there are issues, ensure that ports 6664 and 8888 are open and that your router allows for UDP broadcasts. 

	-The images rendered by the clients will save to the directory selected during the Network creation process. You can compile those images into a video however you want.

	-You can close the network when you are done by either using the 'Close Network' option in the menus or by closing the fractal editor. The client computers do not need to only render the fractals: feel free to use them while they render.


Creating new layer types:

	-If you wish to create and use your own, custom layer types, you are free to do so. There are two ways to add them to the program. 

	-The easiest is to place the .java file in the 'custom' folder or, if you have a .class file, place that in the 'custom/fractal' folder. Upon startup, any layer files located in these folders will be added to the registry and made usable.

	-Alternatively, if you have the .java file, you can use the 'New Layer Type' menu to import it into the application without restarting it. This layer type will be removed if the application is retarted unless the .java file is in the 'custom' folder.

	-IMPORTANT: Adding custom layers to the application is done through dynamic rendering with the Reflections API. This will ONLY work if the application is run through a jdk. If it is run with a jre, your custm layers will not be added.

	-The actual programming of the layer file is more complicated. Start by downloading the source code for this application. Then, create a new class that extends Layer. From there, you must implement a number of abstract methods in order for the layer to work. For more information on what needs to be implements and what the methods do, refer to the Layer documentation and the HistogramLayer and TriangleAverageLayer source code.


DAVID JOBS
Add editable parameters to parameters section
Make GUI even on all screen sizes
Add layer movement controlls
Make a tooltip in the layer editor display the description for each layer type


CALVIN JOBS
Add anti-alliasing and super sampling to rendering


NO-ONE JOBS
Fix glitch where app continues running after menu's closed
Document code properly
Generate JavaDocs
Do project writeup


