# JSweet Examples (threejs framework)

Some examples to demonstrate using the awesome http://threejs.org framework (WebGL powered 3D) from Java thanks to the [JSweet tranpiler](https://github.com/cincheo/jsweet).

- [`webgl_interactive_cubes.html`] An example showing cubes, which you can highlight by clicking. [[browse](http://examples.jsweet.org/jsweet-examples-threejs/webapp/webgl_interactive_cubes.html)]
- [`webgl_materials_normalmap.html`] An example showing Lee Perry-Smith (Infinite-Realities). [[browse](http://examples.jsweet.org/jsweet-examples-threejs/webapp/webgl_materials_normalmap.html)]

These examples are direct translations to JSweet of some of the JavaScript examples taken from the threejs web site. Of course, copyrights remains fully to the original authors.

Check out more [JSweet examples](https://github.com/cincheo/jsweet).

## Usage

```
> git clone https://github.com/cincheo/jsweet-examples-threejs.git
> cd jsweet-examples-threejs
> mvn generate-sources
> firefox webapp/${example}.html
```

## Prerequisites

The `node` and `npm` executables must be in the path (https://nodejs.org).
Install Maven (https://maven.apache.org/install.html).
