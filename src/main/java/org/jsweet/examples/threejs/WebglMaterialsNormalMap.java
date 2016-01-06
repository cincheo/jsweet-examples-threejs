package org.jsweet.examples.threejs;

import static ambient.three.Globals.BleachBypassShader;
import static ambient.three.Globals.ColorCorrectionShader;
import static ambient.three.Globals.FXAAShader;
import static def.threejs.Globals.Detector;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.requestAnimationFrame;
import static jsweet.dom.Globals.window;
import static jsweet.util.Globals.union;

import def.stats.Stats;
import def.threejs.three.AmbientLight;
import def.threejs.three.DirectionalLight;
import def.threejs.three.EffectComposer;
import def.threejs.three.Euler;
import def.threejs.three.Geometry;
import def.threejs.three.ImageUtils;
import def.threejs.three.JSONLoader;
import def.threejs.three.Material;
import def.threejs.three.Mesh;
import def.threejs.three.MeshPhongMaterial;
import def.threejs.three.MeshPhongMaterialParameters;
import def.threejs.three.PerspectiveCamera;
import def.threejs.three.PointLight;
import def.threejs.three.RenderPass;
import def.threejs.three.Scene;
import def.threejs.three.ShaderPass;
import def.threejs.three.Vector2;
import def.threejs.three.WebGLRenderer;
import def.threejs.three.WebGLRendererParameters;
import jsweet.dom.Event;
import jsweet.dom.HTMLElement;
import jsweet.dom.MouseEvent;
import jsweet.dom.TouchEvent;
import jsweet.util.StringTypes;

public class WebglMaterialsNormalMap {

	static double SCREEN_WIDTH, SCREEN_HEIGHT;

	static boolean statsEnabled = true;

	static HTMLElement container;
	static Stats stats;
	static JSONLoader loader;

	static PerspectiveCamera camera;
	static Scene scene;
	static WebGLRenderer renderer;

	static Mesh mesh, zmesh, lightMesh, geometry;
	static Mesh mesh1;

	static AmbientLight ambientLight;
	static DirectionalLight directionalLight;
	static PointLight pointLight;

	static double mouseX = 0;
	static double mouseY = 0;

	static double windowHalfX = window.innerWidth / 2;
	static double windowHalfY = window.innerHeight / 2;

	static ShaderPass effectFXAA;
	static EffectComposer composer;

	public static void main(String[] args) {
		if (!Detector.webgl)
			Detector.addGetWebGLMessage();

		init();
		animate(0);
	}

	public static void init() {

		container = document.createElement("div");
		document.body.appendChild(container);

		camera = new PerspectiveCamera(27, window.innerWidth / window.innerHeight, 1, 10000);
		camera.position.z = 1200;

		scene = new Scene();

		// LIGHTS

		ambientLight = new AmbientLight(0x444444);
		scene.add(ambientLight);

		pointLight = new PointLight(0xffffff, 1.25, 1000);
		pointLight.position.set(0, 0, 600);

		scene.add(pointLight);

		directionalLight = new DirectionalLight(0xffffff);
		directionalLight.position.set(1, -0.5, -1);
		scene.add(directionalLight);

		Material material = new MeshPhongMaterial(new MeshPhongMaterialParameters() {
			{
				color = union(0xdddddd);
				specular = 0x222222;
				shininess = 35;
				map = ImageUtils.loadTexture("obj/leeperrysmith/Map-COL.jpg");
				specularMap = ImageUtils.loadTexture("obj/leeperrysmith/Map-SPEC.jpg");
				normalMap = ImageUtils.loadTexture("obj/leeperrysmith/Infinite-Level_02_Tangent_SmoothUV.jpg");
				normalScale = new Vector2(0.8, 0.8);
			}
		});

		loader = new JSONLoader();
		loader.load("obj/leeperrysmith/LeePerrySmith.js", (geometry, materials) -> {
			createScene(geometry, 100, material);
		});

		renderer = new WebGLRenderer(new WebGLRendererParameters() {
			{
				antialias = false;
			}
		});
		renderer.setClearColor(0x111111);
		renderer.setPixelRatio(window.devicePixelRatio);
		renderer.setSize(window.innerWidth, window.innerHeight);
		container.appendChild(renderer.domElement);

		//

		renderer.gammaInput = true;
		renderer.gammaOutput = true;

		//

		if (statsEnabled) {

			stats = new Stats();
			stats.domElement.style.position = "absolute";
			stats.domElement.style.top = "0px";
			stats.domElement.style.zIndex = "100";
			container.appendChild(stats.domElement);

		}

		// COMPOSER

		renderer.autoClear = false;

		RenderPass renderModel = new RenderPass(scene, camera);

		ShaderPass effectBleach = new ShaderPass(BleachBypassShader);
		ShaderPass effectColor = new ShaderPass(ColorCorrectionShader);
		effectFXAA = new ShaderPass(FXAAShader);

		((Euler) effectFXAA.$get("uniforms").$get("resolution").$get("value")).set(1 / window.innerWidth,
				1 / window.innerHeight, 0);

		effectBleach.$get("uniforms").$get("opacity").$set("value", 0.4);

		((Euler) effectColor.$get("uniforms").$get("powRGB").$get("value")).set(1.4, 1.45, 1.45);
		((Euler) effectColor.$get("uniforms").$get("mulRGB").$get("value")).set(1.1, 1.1, 1.1);

		effectFXAA.renderToScreen = true;

		composer = new EffectComposer(renderer);

		composer.addPass(renderModel);

		composer.addPass(effectBleach);
		composer.addPass(effectColor);
		composer.addPass(effectFXAA);

		// EVENTS

		document.addEventListener(StringTypes.mousemove, WebglMaterialsNormalMap::onDocumentMouseMove, false);
		document.addEventListener(StringTypes.touchmove, WebglMaterialsNormalMap::onDocumentTouchMove, false);
		window.addEventListener("resize", WebglMaterialsNormalMap::onWindowResize, false);

	}

	public static void createScene(Geometry geometry, double scale, Material material) {

		mesh1 = new Mesh(geometry, material);

		mesh1.position.y = -50;
		mesh1.scale.x = mesh1.scale.y = mesh1.scale.z = scale;

		scene.add(mesh1);

	}

	//

	public static void onWindowResize(Event event) {

		SCREEN_WIDTH = window.innerWidth;
		SCREEN_HEIGHT = window.innerHeight;

		renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		camera.aspect = SCREEN_WIDTH / SCREEN_HEIGHT;
		camera.updateProjectionMatrix();

		composer.reset();

		((Euler) effectFXAA.$get("uniforms").$get("resolution").$get("value")).set(1 / SCREEN_WIDTH, 1 / SCREEN_HEIGHT,
				0);

	}

	public static Object onDocumentMouseMove(MouseEvent event) {

		mouseX = (event.clientX - windowHalfX) * 10;
		mouseY = (event.clientY - windowHalfY) * 10;

		return null;
	}

	public static Object onDocumentTouchMove(TouchEvent event) {

		mouseX = (event.touches.$get(0).clientX - windowHalfX) * 10;
		mouseY = (event.touches.$get(0).clientY - windowHalfY) * 10;

		return null;
	}

	//

	public static void animate(double time) {

		requestAnimationFrame(WebglMaterialsNormalMap::animate);

		render();
		if (statsEnabled)
			stats.update();

	}

	public static void render() {

		double ry = mouseX * 0.0003, rx = mouseY * 0.0003;

		if (mesh1 != null) {

			mesh1.rotation.y = ry;
			mesh1.rotation.x = rx;

		}

		// renderer.render( scene, camera );
		composer.render();

	}

}
