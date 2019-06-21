/*
Author:fuchao.wang@nio.com
Version:190322
Date:3/25/2019
Function:GEO ISO CP plots
*/
package macro;
import java.util.*;
import java.util.regex.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;
import star.flow.*;
import java.io.*;
import java.math.*;
import star.base.report.*;
import star.meshing.*;

public class Super_Scene_Beta_0424 extends StarMacro {

public void execute() {
try{
	
	createFunctionISO();
	createFunctionCpx();
	clearAllScene();
	// get region & boundary********
	Collection cRegion = getAllRegionRegex();
	Vector vBoundary = getOnlyBoundaryRegex(cRegion,"_(?i)tunnel(\\.)");
	Vector vRegionBoundary = comRegionBoundary(cRegion,vBoundary);
	IsoPart isosurface = createIsoDerived(vRegionBoundary);
	Vector vIso = new Vector();
	
	String Symmetry_1 = findBoundary(cRegion,"Symmetry");
	if (Symmetry_1 != ""){
		fixSymmetry(findBDbyName(cRegion,Symmetry_1));
		}
	
	//**CREATE SCENE*****
	Scene scene_00 = createGeoSurfaceScene(vBoundary,Symmetry_1);
	printScene2pic(vBoundary,scene_00,createNameAnnotation(scene_00),createLogoAnnotation(scene_00),vIso);
	//
	//***** unvisible colorbar ******
	Scene scene_20 = createCpScene(vBoundary,Symmetry_1,"unvisible");
	printScene2pic(vBoundary,scene_20,createNameAnnotation(scene_20),createLogoAnnotation(scene_20),vIso);
	printColorbarOnly(scene_20);
	//
	Scene scene_30 = createCpxScene(vBoundary,Symmetry_1,"unvisible");
	printScene2pic(vBoundary,scene_30,createNameAnnotation(scene_30),createLogoAnnotation(scene_30),vIso);
	printColorbarOnly(scene_30);
	////set for iso surface
	////
	vIso.add(isosurface);
	Scene scene_10 = createIsoSurfaceScene(vBoundary,isosurface,Symmetry_1);
	printScene2pic(vBoundary,scene_10,createNameAnnotation(scene_10),createLogoAnnotation(scene_10),vIso);
	
	//**print section
	printYSection2pic(vBoundary,Symmetry_1,"",vIso);
	printXSection2pic(vBoundary,Symmetry_1,"",vIso);
	printZSection2pic(vBoundary,Symmetry_1,"",vIso);
	vIso.clear();
	
	//
	//////**EXTRACT TABLES*******
	AccumulatedForceTable accumulatedForceTable_1 = extractTables(vBoundary);
	////
	//////**CREATE PLOTS*********
	creatPlots(accumulatedForceTable_1);
	//
	//**write value to file
	outValue(getReportValue());
	

/*
  clearAllScene();
*/
}
catch (IOException  ex){
		// insert code to run when exception occurs
	}
}


//print scene
private SimpleAnnotation createNameAnnotation(Scene scene11){
	Simulation simulation_0 = getActiveSimulation();
	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	
	//***add case name annotation
	//String[] splitname = presentationName.split("meshed");
	//String purename = splitname[0]+"_"+splitname[1]+"_"+splitname[2]+"_"+splitname[3]+"_"+splitname[4];
	SimpleAnnotation simpleAnnotation_0 = simulation_0.getAnnotationManager().createSimpleAnnotation();
	simpleAnnotation_0.setText(presentationName);
	simpleAnnotation_0.setFontString("Blue Sky Standard Light-Italic");
	simpleAnnotation_0.setOpacity(0.05);
	return simpleAnnotation_0;
}

private ImageAnnotation2D createLogoAnnotation(Scene scene12){
	Simulation simulation_0 = getActiveSimulation();
	ImageAnnotation2D imageAnnotation2D_0 = simulation_0.getAnnotationManager().createImageAnnotation2D();
	imageAnnotation2D_0.setFilePath("/mnt/hpcdata/CFDCentralStorage/Utilities/NIO_AERO_LOGO.png");
	return imageAnnotation2D_0;
	
}

private void printScene2pic(Vector vBoundary, Scene scene_10,SimpleAnnotation simpleAnnotation_0,ImageAnnotation2D imageAnnotation2D_0, Vector vIso){
	scene_10.setAxesVisible(false);
	Simulation simulation_0 = getActiveSimulation();
	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	//String[] splitname = presentationName.split("_");
	//String purename = splitname[0]+"_"+splitname[1]+"_"+splitname[2]+"_"+splitname[3]+"_"+splitname[4];
	
	//***add case name annotation
	//SceneUpdate sceneUpdate_1 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_1 = sceneUpdate_1.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_0 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_0);
	//hardcopyProperties_1.setCurrentResolutionHeight(1000);
	fixedAspectAnnotationProp_0.setPosition(new DoubleVector(new double[] {0.005, 0.0001, 0.0}));
	fixedAspectAnnotationProp_0.setHeight(0.03);
	//***add logo annotation
	//SceneUpdate sceneUpdate_2 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(imageAnnotation2D_0);
	fixedAspectAnnotationProp_1.setHeight(0.08);
	//hardcopyProperties_2.setCurrentResolutionHeight(2000);
	fixedAspectAnnotationProp_1.setPosition(new DoubleVector(new double[] {0.008, 0.92, 0.0}));

//*****new printer
	int hardcopy_width = 1280;
	String[] view_norm = {"X", "-X", "Y", "-Y", "Z", "-Z", "45", "-1, -1, 0.5"};
	String[] view_up = {"Z", "Z", "Z", "Z", "+Y", "-Y", "Z", "Z"};
	
if (vIso.isEmpty()){
}else{
	vBoundary.add(vIso.firstElement());
}
	for (int i = 0; i < view_norm.length; i++)
	{
		String scenepngname = ("1"+i+"_"+view_norm[i]+"_"+scene_10);
		double AspectRatio = set_view(vBoundary, view_norm[i], view_up[i], 0.06, scene_10);
		hardcopy_width = (int) Math.ceil(AspectRatio*720);
		scene_10.printAndWait(resolvePath("/"+AbsPath+"/"+presentationName+".post/SuperScene/"+scenepngname+".png"), 2, hardcopy_width, 720, true, false);
		getSimulation().println("print "+scenepngname+" png ----- DONE");
	}
	simulation_0.getAnnotationManager().removeObjects(simpleAnnotation_0);
	simulation_0.getAnnotationManager().removeObjects(imageAnnotation2D_0);
}   



//////*********printsection 
private SimpleAnnotation createSectionAnnotation(Scene scene11,String direction,int distance){
	Simulation simulation_0 = getActiveSimulation();
	String purename = (direction+" = "+distance);
	SimpleAnnotation simpleAnnotation_0 = simulation_0.getAnnotationManager().createSimpleAnnotation();
	simpleAnnotation_0.setText(purename);
	simpleAnnotation_0.setFontString("Blue Sky Standard Light-Italic");
	simpleAnnotation_0.setOpacity(1);
	return simpleAnnotation_0;
}

private Scene createSliceScene(Vector vBoundary,String Symmetry_1,PlaneSection planeSection_0,String setColorbar){

	Simulation simulation_0 = getActiveSimulation();
	Scene scene_0 = simulation_0.getSceneManager().createScene("PlaneSection");
	scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
	scene_0.setAdvancedRenderingEnabled(true);
	LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));
	scene_0.getAnnotationPropManager().removePropForAnnotation(logoAnnotation_0);
	ScalarDisplayer scalarDisplayer_0 = scene_0.getDisplayerManager().createScalarDisplayer("Scalar");
	scalarDisplayer_0.setFillMode(ScalarFillMode.NODE_FILLED);
	scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);
	scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
	scalarDisplayer_0.initialize();
	scalarDisplayer_0.getInputParts().setQuery(null);
	scalarDisplayer_0.getInputParts().setObjects(planeSection_0);
	setScalar(scalarDisplayer_0);
	//set color bar
	Legend legend_0 = scalarDisplayer_0.getLegend();
	legend_0.setPositionCoordinate(new DoubleVector(new double[] {0.21, 0.05}));
	legend_0.setLabelFormat("%-#9.2f");
	legend_0.setNumberOfLabels(11);
	if (setColorbar == "unvisible"){
	legend_0.setVisible(false);
	}
	//scene_0.open(true);
	if (Symmetry_1 != ("")){
	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager().getObject(Symmetry_1));
	scalarDisplayer_0.setVisTransform(symmetricRepeat_0);
	}
	getSimulation().println("**Create Section ----- DONE");
	return scene_0;
}

private ScalarDisplayer setScalar(ScalarDisplayer scalarDisplayer_0){
	Simulation simulation_0 = getActiveSimulation();
	PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));
	VectorMagnitudeFieldFunction vectorMagnitudeFieldFunction_0 = ((VectorMagnitudeFieldFunction) primitiveFieldFunction_0.getMagnitudeFunction());
	scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(vectorMagnitudeFieldFunction_0);
	scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
	scalarDisplayer_0.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] {0.0, 50.0}));
	return scalarDisplayer_0;
}

private PlaneSection createSectionDerived(String direction,int[] defaultSectionPara){
    Simulation simulation_0 = getActiveSimulation();
    PlaneSection planeSection_0 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] {defaultSectionPara[0], defaultSectionPara[1], defaultSectionPara[2]}), new DoubleVector(new double[] {defaultSectionPara[3], defaultSectionPara[4], defaultSectionPara[5]}), 0, 1, new DoubleVector(new double[] {0.0}));
    Coordinate coordinate_0 = planeSection_0.getOriginCoordinate();
    Units units_0 = ((Units) simulation_0.getUnitsManager().getObject("mm"));
    coordinate_0.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {defaultSectionPara[3], defaultSectionPara[4], defaultSectionPara[5]}));
    planeSection_0.getInputParts().setQuery(null);
    Vector vRegions = new Vector(simulation_0.getRegionManager().getRegions());
    planeSection_0.getInputParts().setObjects(vRegions);
    planeSection_0.setPresentationName(direction+"Scetion"+defaultSectionPara[6]);
	return planeSection_0;
}

private PlaneSection moveSectionDerived(PlaneSection planeSection_0,int distance){
	SingleValue singleValue_0 = planeSection_0.getSingleValue();
	singleValue_0.getValueQuantity().setValue(distance);
	return planeSection_0;
}

private int printSection2pic(Vector vBoundary, Scene scene_10,SimpleAnnotation simpleAnnotation_0,ImageAnnotation2D imageAnnotation2D_0,SimpleAnnotation simpleAnnotation_1,String[] view_norm,String[] view_up,int[] defaultSectionPara){
	scene_10.setAxesVisible(false);
	Simulation simulation_0 = getActiveSimulation();
	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	//String[] splitname = presentationName.split("_");
	//String purename = (""+distance);
	
	//***add case name annotation
	//SceneUpdate sceneUpdate_1 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_1 = sceneUpdate_1.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_0 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_0);
	//hardcopyProperties_1.setCurrentResolutionHeight(1000);
	fixedAspectAnnotationProp_0.setPosition(new DoubleVector(new double[] {0.005, 0.0001, 0.0}));
	fixedAspectAnnotationProp_0.setHeight(0.03);
	
	//***add logo annotation
	//SceneUpdate sceneUpdate_2 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(imageAnnotation2D_0);
	fixedAspectAnnotationProp_1.setHeight(0.08);
	//hardcopyProperties_2.setCurrentResolutionHeight(2000);
	fixedAspectAnnotationProp_1.setPosition(new DoubleVector(new double[] {0.008, 0.92, 0.0}));
		//***add case name annotation
	//SceneUpdate sceneUpdate_3 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_3 = sceneUpdate_3.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_3 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);
	//hardcopyProperties_3.setCurrentResolutionHeight(1000);
	fixedAspectAnnotationProp_3.setPosition(new DoubleVector(new double[] {0.005, 0.031, 0.0}));
	fixedAspectAnnotationProp_3.setHeight(0.03);

//*****new printer
	int hardcopy_width = 1280;
	//String[] view_norm = {"-Y"};
	//String[] view_up = {"Z"};
		String scenepngname = (""+defaultSectionPara[6]);
		double AspectRatio = set_view(vBoundary, view_norm[0], view_up[0], 0.3, scene_10);
		hardcopy_width = (int) Math.ceil(AspectRatio*720);
		scene_10.printAndWait(resolvePath("/"+AbsPath+"/"+presentationName+".post/SuperScene/"+view_norm[0]+"_Section/"+scenepngname+".png"), 2, hardcopy_width, 720, true, false);
		getSimulation().println("print "+view_norm[0]+"Section_"+scenepngname+" png ----- DONE");
	//simulation_0.getAnnotationManager().removeObjects(simpleAnnotation_0);
	//simulation_0.getAnnotationManager().removeObjects(imageAnnotation2D_0);
	simulation_0.getAnnotationManager().removeObjects(simpleAnnotation_1);
	return hardcopy_width;
}   

private void printSection2picDirec(int hardcopy_width,Vector vBoundary, Scene scene_10,SimpleAnnotation simpleAnnotation_1,String[] view_norm,String[] view_up,int[] defaultSectionPara){
	Simulation simulation_0 = getActiveSimulation();

	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	String scenepngname = (""+defaultSectionPara[6]);
	
		//***add case name annotation
	//SceneUpdate sceneUpdate_1 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_1 = sceneUpdate_1.getHardcopyProperties();
	//FixedAspectAnnotationProp fixedAspectAnnotationProp_0 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_0);
	////hardcopyProperties_1.setCurrentResolutionHeight(1000);
	//fixedAspectAnnotationProp_0.setPosition(new DoubleVector(new double[] {0.005, 0.0001, 0.0}));
	//fixedAspectAnnotationProp_0.setHeight(0.03);
	//
	////***add logo annotation
	////SceneUpdate sceneUpdate_2 = scene_10.getSceneUpdate();
	////HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();
	//FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(imageAnnotation2D_0);
	//fixedAspectAnnotationProp_1.setHeight(0.08);
	////hardcopyProperties_2.setCurrentResolutionHeight(2000);
	//fixedAspectAnnotationProp_1.setPosition(new DoubleVector(new double[] {0.008, 0.92, 0.0}));
		//***add case name annotation
	//SceneUpdate sceneUpdate_3 = scene_10.getSceneUpdate();
	//HardcopyProperties hardcopyProperties_3 = sceneUpdate_3.getHardcopyProperties();
	FixedAspectAnnotationProp fixedAspectAnnotationProp_3 = (FixedAspectAnnotationProp) scene_10.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);
	//hardcopyProperties_3.setCurrentResolutionHeight(1000);
	fixedAspectAnnotationProp_3.setPosition(new DoubleVector(new double[] {0.005, 0.031, 0.0}));
	fixedAspectAnnotationProp_3.setHeight(0.03);
	
	scene_10.printAndWait(resolvePath("/"+AbsPath+"/"+presentationName+".post/SuperScene/"+view_norm[0]+"_Section/"+scenepngname+".png"), 2, hardcopy_width, 720, true, false);
	getSimulation().println("print "+view_norm[0]+"Section_"+scenepngname+" png ----- DONE");
	//simulation_0.getAnnotationManager().removeObjects(simpleAnnotation_0);
	//simulation_0.getAnnotationManager().removeObjects(imageAnnotation2D_0);
	simulation_0.getAnnotationManager().removeObjects(simpleAnnotation_1);
}   

//Print in XYZ direction
private void printYSection2pic(Vector vBoundary,String Symmetry_1,String setColorbar,Vector vIso){
	int[] defaultSectionPara = {0,1,0,0,0,0,100000};
	String[] view_norm = {"-Y"};
	String[] view_up = {"Z"};
	String direction = view_norm[0];
	PlaneSection planeSection_0 = createSectionDerived(direction,defaultSectionPara);
	Scene scene_40 = createSliceScene(vBoundary,Symmetry_1,planeSection_0,"");
	int distance=0;
	defaultSectionPara[6] = 100000+distance;
	planeSection_0 = moveSectionDerived(planeSection_0, -2);
	int hardcopy_width = printSection2pic(vIso,scene_40,createNameAnnotation(scene_40),createLogoAnnotation(scene_40),createSectionAnnotation(scene_40,direction,distance),view_norm,view_up,defaultSectionPara);
	
	for (int j=20;j<1200;j+=20){
	int distance_1=0-j;
	planeSection_0 = moveSectionDerived(planeSection_0, distance_1);
	defaultSectionPara[6] = 100000+Math.abs(j);
	//printSection2pic(vIso,scene_40,createNameAnnotation(scene_40),createLogoAnnotation(scene_40),createSectionAnnotation(scene_40,direction,distance_1),view_norm,view_up,defaultSectionPara);
	printSection2picDirec(hardcopy_width,vIso,scene_40,createSectionAnnotation(scene_40,direction,distance_1),view_norm,view_up,defaultSectionPara);

	}
}


private void printXSection2pic(Vector vBoundary,String Symmetry_1,String setColorbar,Vector vIso){
	int[] defaultSectionPara = {1,0,0,0,0,0,200000};
	String[] view_norm = {"X"};
	String[] view_up = {"Z"};
	String direction = view_norm[0];
	PlaneSection planeSection_0 = createSectionDerived(direction,defaultSectionPara);
	Scene scene_40 = createSliceScene(vBoundary,Symmetry_1,planeSection_0,"");
	int distance=-1300;
	defaultSectionPara[6] = 200000+distance;
	planeSection_0 = moveSectionDerived(planeSection_0, distance);
	int hardcopy_width = printSection2pic(vIso,scene_40,createNameAnnotation(scene_40),createLogoAnnotation(scene_40),createSectionAnnotation(scene_40,direction,distance),view_norm,view_up,defaultSectionPara);
	
	for (int j=-1250;j<5700;j+=50){
	int distance_1=j;
	defaultSectionPara[6] = 200000+distance_1;
	planeSection_0 = moveSectionDerived(planeSection_0, distance_1);
	printSection2picDirec(hardcopy_width,vIso,scene_40,createSectionAnnotation(scene_40,direction,distance_1),view_norm,view_up,defaultSectionPara);
	}
}


private void printZSection2pic(Vector vBoundary,String Symmetry_1,String setColorbar,Vector vIso){
	int[] defaultSectionPara = {0,0,1,0,0,0,300000};
	String[] view_norm = {"-Z"};
	String[] view_up = {"-Y"};
	String direction = view_norm[0];
	PlaneSection planeSection_0 = createSectionDerived(direction,defaultSectionPara);
	Scene scene_40 = createSliceScene(vBoundary,Symmetry_1,planeSection_0,"");
	int distance=-360;
	planeSection_0 = moveSectionDerived(planeSection_0, distance);
	int hardcopy_width = printSection2pic(vIso,scene_40,createNameAnnotation(scene_40),createLogoAnnotation(scene_40),createSectionAnnotation(scene_40,direction,distance),view_norm,view_up,defaultSectionPara);
	for (int j=-320;j<1800;j+=40){
	int distance_1=j;
	defaultSectionPara[6] = 300000+distance_1;
	planeSection_0 = moveSectionDerived(planeSection_0, distance_1);
	printSection2picDirec(hardcopy_width,vIso,scene_40,createSectionAnnotation(scene_40,direction,distance_1),view_norm,view_up,defaultSectionPara);
	}

}




//create iso surface part
private IsoPart createIsoDerived(Vector vBoundary){
	Simulation simulation_0 = getActiveSimulation();
	//ues PTC function***
	UserFieldFunction userFieldFunction_0 = ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("__PTC"));
	IsoPart isoPart_1 = simulation_0.getPartManager().createIsoPart(new NeoObjectVector(new Object[] {}), userFieldFunction_0);
	isoPart_1.setMode(IsoMode.ISOVALUE_SINGLE);
	SingleIsoValue singleIsoValue_0 = isoPart_1.getSingleIsoValue();
	singleIsoValue_0.getValueQuantity().setValue(0.0);
	isoPart_1.getInputParts().setQuery(null);
	isoPart_1.getInputParts().setObjects(vBoundary);
	return isoPart_1;
}

//CREATE ISO SCENE***
private Scene createIsoSurfaceScene(Vector vBoundary,IsoPart isoSurface,String Symmetry_1) {
	
	Simulation simulation_0 = getActiveSimulation();
	Scene scene_0 = simulation_0.getSceneManager().createScene("ISO");
	scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
	LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));
	scene_0.getAnnotationPropManager().removePropForAnnotation(logoAnnotation_0);
	scene_0.setAdvancedRenderingEnabled(true);
	PartDisplayer partDisplayer_1 = scene_0.getDisplayerManager().createPartDisplayer("ISO", -1, 4);
	PartDisplayer partDisplayer_0 = scene_0.getDisplayerManager().createPartDisplayer("BODY", -1, 4);
	partDisplayer_0.setColorMode(PartColorMode.CONSTANT);
	partDisplayer_0.setSurface(true);
	partDisplayer_0.getInputParts().setQuery(null);
	partDisplayer_0.getInputParts().setObjects(vBoundary);
	partDisplayer_1.getInputParts().setQuery(null);
	partDisplayer_1.getInputParts().setObjects(isoSurface);
	partDisplayer_1.setSurface(true);
	//**identify HV/FV to set sym***
	if (Symmetry_1 != ("")){
	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager().getObject(Symmetry_1));
	partDisplayer_0.setVisTransform(symmetricRepeat_0);
	partDisplayer_1.setVisTransform(symmetricRepeat_0);
	partDisplayer_1.setColorMode(PartColorMode.DEFAULT);
	}
	getSimulation().println("**Create Iso surface ----- DONE");
	return scene_0;
}

//CREATE GEO SURFACE SCENE***
private Scene createGeoSurfaceScene(Vector vBoundary,String Symmetry_1) {
	Simulation simulation_0 = getActiveSimulation();
	Scene scene_0 = simulation_0.getSceneManager().createScene("Geo");
	scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
	LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));
	scene_0.getAnnotationPropManager().removePropForAnnotation(logoAnnotation_0);
	scene_0.setAdvancedRenderingEnabled(true);
	PartDisplayer partDisplayer_0 = scene_0.getDisplayerManager().createPartDisplayer("Geo", -1, 4);
	partDisplayer_0.initialize();
	partDisplayer_0.setColorMode(PartColorMode.CONSTANT);
	partDisplayer_0.setSurface(true);
	partDisplayer_0.getInputParts().setQuery(null);
	partDisplayer_0.getInputParts().setObjects(vBoundary);
	//identify HV/FV to set sym
	if (Symmetry_1 != ("")){
	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager().getObject(Symmetry_1));
	partDisplayer_0.setVisTransform(symmetricRepeat_0);
	}
	getSimulation().println("**Create Geo surface ----- DONE");
	return scene_0;
}

//CREATE CP SCENE***
private Scene createCpScene(Vector vBoundary,String Symmetry_1,String setColorbar) {
	Simulation simulation_0 = getActiveSimulation();
	Scene scene_0 = simulation_0.getSceneManager().createScene("Cp");
	scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
	scene_0.setAdvancedRenderingEnabled(true);
	LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));
	scene_0.getAnnotationPropManager().removePropForAnnotation(logoAnnotation_0);
	ScalarDisplayer scalarDisplayer_0 = scene_0.getDisplayerManager().createScalarDisplayer("Scalar");
	scalarDisplayer_0.initialize();
	scalarDisplayer_0.getInputParts().setQuery(null);
	scalarDisplayer_0.getInputParts().setObjects(vBoundary);
	PressureCoefficientFunction pressureCoefficientFunction_0 =
	  ((PressureCoefficientFunction) simulation_0.getFieldFunctionManager().getFunction("PressureCoefficient"));
	scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(pressureCoefficientFunction_0);
	scalarDisplayer_0.setFillMode(ScalarFillMode.NODE_FILLED);
    scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);
    scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
	scalarDisplayer_0.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] {-0.5, 0.0}));
	//set color bar
	Legend legend_0 = scalarDisplayer_0.getLegend();
	legend_0.setPositionCoordinate(new DoubleVector(new double[] {0.21, 0.05}));
	legend_0.setLabelFormat("%-#9.2f");
	legend_0.setNumberOfLabels(11);
	if (setColorbar == "unvisible"){
	legend_0.setVisible(false);
	}
	//scene_0.open(true);
	if (Symmetry_1 != ("")){
	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager().getObject(Symmetry_1));
	scalarDisplayer_0.setVisTransform(symmetricRepeat_0);
	}
	getSimulation().println("**Create Cp ----- DONE");
	return scene_0;
  }

//create Cpx
private Scene createCpxScene(Vector vBoundary,String Symmetry_1,String setColorbar) {
	Simulation simulation_0 = getActiveSimulation();
	Scene scene_0 = simulation_0.getSceneManager().createScene("Cpx");
	//##scene_0.setBackgroundColorMode(0);//ONLY 10.06
	scene_0.setBackgroundColorMode(BackgroundColorMode.SOLID);
	LogoAnnotation logoAnnotation_0 = ((LogoAnnotation) simulation_0.getAnnotationManager().getObject("Logo"));
	scene_0.getAnnotationPropManager().removePropForAnnotation(logoAnnotation_0);
	scene_0.setAdvancedRenderingEnabled(true);
	//CurrentView currentView_0 = scene_0.getCurrentView();
	//currentView_0.setInput(new DoubleVector(new double[] {view[0], view[1], view[2]}), new DoubleVector(new double[] {view[3], view[4], view[5]}), new DoubleVector(new double[] {view[6], view[7], view[8]}), view[9], 1);
	//##currentView_0.updateCurrentView();//ONLY 10.06
	//currentView_0.applyAndUpdate();
	ScalarDisplayer scalarDisplayer_0 = scene_0.getDisplayerManager().createScalarDisplayer("Scalar");
	scalarDisplayer_0.initialize();
	scalarDisplayer_0.getInputParts().setQuery(null);
	scalarDisplayer_0.getInputParts().setObjects(vBoundary);
	UserFieldFunction userFieldFunction_0 = ((UserFieldFunction) simulation_0.getFieldFunctionManager().getFunction("__Cpx"));
	scalarDisplayer_0.getScalarDisplayQuantity().setFieldFunction(userFieldFunction_0);
	//##scalarDisplayer_0.getScalarDisplayQuantity().setClip(0);//ONLY 10.06
	//##scalarDisplayer_0.setFillMode(1);//ONLY 10.06
	scalarDisplayer_0.setFillMode(ScalarFillMode.NODE_FILLED);
    scalarDisplayer_0.getScalarDisplayQuantity().setClip(ClipMode.NONE);
    scalarDisplayer_0.getScalarDisplayQuantity().setAutoRange(AutoRangeMode.NONE);
	scalarDisplayer_0.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] {-1.0, 1.0}));
	//set color bar
	Legend legend_0 = scalarDisplayer_0.getLegend();
	CoolWarmLookupTable coolWarmLookupTable_0 = ((CoolWarmLookupTable) simulation_0.get(LookupTableManager.class).getObject("cool-warm"));
	legend_0.setLookupTable(coolWarmLookupTable_0);
	legend_0.setPositionCoordinate(new DoubleVector(new double[] {0.21, 0.05}));
	legend_0.setLabelFormat("%-#9.2f");
	legend_0.setNumberOfLabels(11);
	if (setColorbar == "unvisible"){
	legend_0.setVisible(false);
	}
	//scene_0.open(true);
	if (Symmetry_1 != ("")){
	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager().getObject(Symmetry_1));
	scalarDisplayer_0.setVisTransform(symmetricRepeat_0);
	}
	//printScene()
	getSimulation().println("**Create Cpx ----- DONE");
	return scene_0;
  }

//create colorbra only
private void printColorbarOnly(Scene scene_11){
	scene_11.getAnnotationPropManager().removePropsForAnnotations();
	scene_11.setAxesVisible(false);
	//scene_11.setAdvancedRenderingEnabled(true);
	Simulation simulation_0 = getActiveSimulation();
	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	//***add annotation
	String[] splitname = presentationName.split("_");
	String purename = splitname[1]+"_"+splitname[2]+"_"+splitname[3]+"_"+splitname[4];
	
	
	ScalarDisplayer scalarDisplayer_1 = ((ScalarDisplayer) scene_11.getDisplayerManager().getDisplayer("Scalar 1"));
	Legend legend_1 = scalarDisplayer_1.getLegend();
	scalarDisplayer_1.getInputParts().setQuery(null);
	scalarDisplayer_1.getInputParts().setObjects();
	scalarDisplayer_1.getScalarDisplayQuantity().setGlobalRangeMode(GlobalRangeMode.ALL_REGIONS);

	legend_1.updateLayout(new DoubleVector(new double[] {0.2, 0.45}), 0.6, 0.04, 0);
	legend_1.setVisible(true);
	String scenepngname = ("colorbar"+scene_11);
	int hardcopy_width = 1280;
	scene_11.printAndWait(resolvePath("/"+AbsPath+"/"+presentationName+".post/SuperScene/"+scenepngname+".png"), 2, hardcopy_width, 720, true, false);
	getSimulation().println("print "+scenepngname+" png ----- DONE");
	
	}
  
  
  
  
//ONLY_BOUNDARY_REGEX**********
private Vector getOnlyBoundaryRegex(Collection cRegion,String regExKey){
	Simulation simulation_0 = getActiveSimulation();
	Vector vObject = new Vector();

	Iterator it = cRegion.iterator();
while(it.hasNext()){
	Region Region_00 = simulation_0.getRegionManager().getRegion(it.next().toString());
	Vector vBoundarys = new Vector(Region_00.getBoundaryManager().getBoundaries());
	vObject.addAll(vBoundarys);
	}
	//vObject.addAll(cRegion);
	String regEx = ".*[\\s].*"+regExKey+".*";
	Pattern pattern = Pattern.compile(regEx);
	for (int i =0 ;i < vObject.size(); i++){
	String currentBoundaryName = vObject.get(i).toString();
	Matcher matcher = pattern.matcher(currentBoundaryName);
	boolean rs = matcher.find();
	if (rs){
	//getSimulation().println(i+""+""+rs);
	getSimulation().println("Regex "+regExKey+": "+currentBoundaryName);
	vObject.remove(i);
	i--;
	}
	}
	getSimulation().println("get Boundary ------------------------- DONE");
	return vObject;
	}
	
	
private String findBoundary(Collection cRegion,String regExKey){
	Simulation simulation_0 = getActiveSimulation();
	Vector vObject = new Vector();
	String fBoundary = "";
	Iterator it = cRegion.iterator();
while(it.hasNext()){
	Region Region_00 = simulation_0.getRegionManager().getRegion(it.next().toString());
	Vector vBoundarys = new Vector(Region_00.getBoundaryManager().getBoundaries());
	vObject.addAll(vBoundarys);
	}
	//vObject.addAll(cRegion);
	String regEx = ".*[\\s].*"+regExKey+".*";
	Pattern pattern = Pattern.compile(regEx);
	for (int i =0 ;i < vObject.size(); i++){
	String currentBoundaryName = vObject.get(i).toString();
	Matcher matcher = pattern.matcher(currentBoundaryName);
	boolean rs = matcher.find();
	if (rs){
	fBoundary = currentBoundaryName;
	break;
	}
	}
	
	if (fBoundary ==""){
		getSimulation().println("NO SYMMETRY, CURRENT MODEL MAY BE A FV MODEL!!");
	return fBoundary;
	}else{
	//getSimulation().println("find Boundary ----- DONE  ");
	fBoundary = fBoundary.replaceAll("\\s*", "");
	String[] fBoundary_a = fBoundary.split(":");
	fBoundary = fBoundary_a[1];
	fBoundary = fBoundary +" 1";
	getSimulation().println("find Boundary ---  "+fBoundary+"  -- DONE");
	return fBoundary;
	}
	}
	

private Boundary findBDbyName(Collection cRegion,String BDname){
	Simulation simulation_0 = getActiveSimulation();
	if (BDname ==""){
		getSimulation().println("NOT FOUND, PLEASE CHECK AGAIN!!");
		return null;
	}else{
	Iterator iter = cRegion.iterator();
	Region region_0 = simulation_0.getRegionManager().getRegion(iter.next().toString());
	String[] BDname_a = BDname.split(" ");
	BDname = BDname_a[0];
	Boundary boundary_0 = region_0.getBoundaryManager().getBoundary(BDname);
	//getSimulation().println("find Boundary ----- DONE  ");
	return boundary_0;
	}
	}

private void fixSymmetry(Boundary boundary_0){
	Simulation simulation_0 = getActiveSimulation();
	WallBoundary wallBoundary_0 = ((WallBoundary) simulation_0.get(ConditionTypeManager.class).get(WallBoundary.class));
    boundary_0.setBoundaryType(wallBoundary_0);
    SymmetryBoundary symmetryBoundary_0 = ((SymmetryBoundary) simulation_0.get(ConditionTypeManager.class).get(SymmetryBoundary.class));
    boundary_0.setBoundaryType(symmetryBoundary_0);
	getSimulation().println("fix Symmetry Boundary ------------------ DONE");
}
	
//ONLY_Region_REGEX******
private Collection getAllRegionRegex(){
	Simulation simulation_0 = getActiveSimulation();
	Collection cRegion = simulation_0.getRegionManager().getRegions();
	/*
	//Collection.toArray() to an Object[]
	Object[] aRegion = cRegion.toArray();
	getSimulation().println(aRegion[0]);
	*/
	String regEx = ".*Cells.*";
	Pattern pattern = Pattern.compile(regEx);
	Iterator it = cRegion.iterator();
	while(it.hasNext()){
	String currentRegionName = it.next().toString();
	Matcher matcher = pattern.matcher(currentRegionName);
	boolean rs = matcher.find();
	if (rs){
	//getSimulation().println(i+""+""+rs);
	getSimulation().println("Regex : "+currentRegionName);
	it.remove();
	}
	}
	//getSimulation().println(cRegion);
	getSimulation().println("get Region ----- DONE");
	return cRegion;
	}

//combine region and  boundary*******
private Vector comRegionBoundary(Collection cRegion,Vector vBoundary){
	Vector vObject = new Vector();
	vObject.addAll(cRegion);
	vObject.addAll(vBoundary);
	return vObject;
	}




//create iso function PTC*****
private void createFunctionISO(){
	Simulation simulation_0 = getActiveSimulation();
	UserFieldFunction userFieldFunction_0 = simulation_0.getFieldFunctionManager().createFieldFunction();
	userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);
	userFieldFunction_0.setFunctionName("__PTC");
	userFieldFunction_0.setPresentationName("__PTC");
	Units units_2 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
	Units units_0 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
	userFieldFunction_0.setDefinition("${WallDistance}<0.001?1:${TotalPressureCoefficient}");
	getSimulation().println("set function __PTC----- DONE");
  }

  //create iso function cpx*****
private void createFunctionCpx(){
	Simulation simulation_0 = getActiveSimulation();
	UserFieldFunction userFieldFunction_0 = simulation_0.getFieldFunctionManager().createFieldFunction();
	userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);
	userFieldFunction_0.setPresentationName("__Cpx");
	userFieldFunction_0.setFunctionName("__Cpx");
	Units units_2 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
	Units units_3 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
	userFieldFunction_0.setDefinition("${PressureCoefficient}*$${Area}[0]/mag($${Area})");
	getSimulation().println("set function __Cpx----- DONE");
}




//EXTRACT TABLES*******
private AccumulatedForceTable extractTables(Vector vBoundary) {
	Simulation simulation_0 = getActiveSimulation();
   // AccumulatedForceTable accumulatedForceTable_0 = ((AccumulatedForceTable) simulation_0.getTableManager().getTable("AccumulatedForceTable"));
	AccumulatedForceTable accumulatedForceTable_0 =
	  simulation_0.getTableManager().createTable(AccumulatedForceTable.class);
	accumulatedForceTable_0.getParts().setObjects(vBoundary);
	FvRepresentation fvRepresentation_0 = ((FvRepresentation) simulation_0.getRepresentationManager().getObject("Volume Mesh"));


	AccumulatedForceHistogram accumulatedForceHistogram_1 = ((AccumulatedForceHistogram) accumulatedForceTable_0.getHistogram());
	accumulatedForceHistogram_1.setNumberOfBin(500);
	accumulatedForceHistogram_1.getProfileDirection().setComponents(0.0, 0.0, 1.0);
	
	//**********************
	accumulatedForceHistogram_1.setNormalizationOption(NormalizationOption.STANDARD);
	
	
	ForceNormalization forceNormalization_0 =
	accumulatedForceHistogram_1.getForceNormalization();
	forceNormalization_0.getReferenceDensity().setValue(1.18415);
	Units units_0 = ((Units) simulation_0.getUnitsManager().getObject("kph"));
	forceNormalization_0.getReferenceVelocity().setUnits(units_0);
	forceNormalization_0.getReferenceVelocity().setValue(140.0);
	Units units_1 = simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
	forceNormalization_0.getReferenceArea().setDefinition("${FrontalAreaReport}");
	accumulatedForceTable_0.setRepresentation(fvRepresentation_0);
	accumulatedForceTable_0.extract();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	String currentName = simulation_0.getPresentationName();
	accumulatedForceTable_0.export(resolvePath("/"+AbsPath+"/"+currentName+".post/"+currentName+"_AccFc.csv"), ",");
	getSimulation().println("extract & export Tables ----- DONE");
	getSimulation().println("/"+AbsPath+"/"+currentName+".post/"+currentName+".csv");
	return accumulatedForceTable_0;
  }

private int countScene() {
Simulation simulation_1 = getActiveSimulation();
Vector vScene = new Vector(simulation_1.get(SceneManager.class).getScenes());
int vSceneLenth = vScene.size();
getSimulation().println("---number ok---");
getSimulation().println("* num of scenes:"+vSceneLenth+" *");
return vSceneLenth;
}




//print all scene
private Scene[] getallScenes() {
Simulation simulation_0 = getActiveSimulation();
Scene[] sScene = simulation_0.get(SceneManager.class).getScenesAsArray();
String arrString = Arrays.toString(sScene);
getSimulation().println("---collect scene ok---");
getSimulation().println("* all scenes: *\n"+arrString);
return sScene;
}

//clear All scene*******
private void clearAllScene() {
	Simulation simulation_0 = getActiveSimulation();
	Vector vScene = new Vector(simulation_0.getSceneManager().getScenes());
//getSimulation().println("*                *");
	getSimulation().println("**REMAIN SCENE "+vScene.toString()+" **");
//getSimulation().println("*                *");
	getSimulation().println("Start remove all SCENE");
	simulation_0.getSceneManager().deleteScenes(vScene);
	getSimulation().println("all SCENE CLEAR");
}




//create plots*******
private void creatPlots(AccumulatedForceTable accumulatedForceTable_1) {
	Simulation simulation_0 = getActiveSimulation();
	String currentName = simulation_0.getPresentationName();
	XYPlot xYPlot_1 = simulation_0.getPlotManager().createPlot(XYPlot.class);

	xYPlot_1.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {accumulatedForceTable_1}));
	xYPlot_1.setPresentationName("02_AccForce");
	ExternalDataSet externalDataSet_0 =
	  ((ExternalDataSet) xYPlot_1.getDataSetManager().getDataSet(accumulatedForceTable_1.getPresentationName()));

	Units units_1 =
	  ((Units) simulation_0.getUnitsManager().getObject("m"));

	LineStyle lineStyle_0 =
	  externalDataSet_0.getLineStyle();
	lineStyle_0.getLinePatternOption().setSelected(LinePatternOption.Type.SOLID);
	lineStyle_0.setWidth(2);

	SymbolStyle symbolStyle_0 =
	  externalDataSet_0.getSymbolStyle();

	symbolStyle_0.getSymbolShapeOption().setSelected(SymbolShapeOption.Type.NONE);

	externalDataSet_0.setSeriesNameLocked(true);
	externalDataSet_0.setSeriesName(currentName);
	externalDataSet_0.setXValuesName("Position");
	externalDataSet_0.setYValuesName("Accumulated Force Coefficient");
	externalDataSet_0.setXUnits(units_1);

	xYPlot_1.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {externalDataSet_0}));
	xYPlot_1.getDataSetManager().addDataProviders(new NeoObjectVector(new Object[] {externalDataSet_0}));

	ExternalDataSet externalDataSet_1 =
	  ((ExternalDataSet) xYPlot_1.getDataSetManager().getDataSet(accumulatedForceTable_1.getPresentationName()+" 2"));

	externalDataSet_1.setSeriesNameLocked(true);
	externalDataSet_1.setSeriesName("");
	externalDataSet_1.setXValuesName("Position");
	externalDataSet_1.setYValuesName("Profile Upper");
	externalDataSet_1.setYScale(0.15);
	externalDataSet_1.setYUnits(units_1);

	ExternalDataSet externalDataSet_2 =
	  ((ExternalDataSet) xYPlot_1.getDataSetManager().getDataSet(accumulatedForceTable_1.getPresentationName()+" 3"));

	externalDataSet_2.setSeriesNameLocked(true);
	externalDataSet_2.setSeriesName("");
	externalDataSet_2.setXValuesName("Position");
	externalDataSet_2.setYValuesName("Profile Lower");
	externalDataSet_2.setYScale(0.15);
	externalDataSet_2.setYUnits(units_1);

	LineStyle lineStyle_1 = externalDataSet_1.getLineStyle();
	lineStyle_1.setColor(new DoubleVector(new double[] {0.0, 0.0, 0.0}));
	LineStyle lineStyle_2 =
	  externalDataSet_2.getLineStyle();
	lineStyle_2.setColor(new DoubleVector(new double[] {0.0, 0.0, 0.0}));
	//xYPlot_1.open();
	MultiColLegend multiColLegend_0 = xYPlot_1.getLegend();
	multiColLegend_0.setRelativePosition(0.06, 0.9);
	//set the plot range*******
	//get the plot range ****
	Cartesian2DAxisManager cartesian2DAxisManager_0 = ((Cartesian2DAxisManager) xYPlot_1.getAxisManager());
	cartesian2DAxisManager_0.setAxesBounds(new Vector(Arrays.asList(new AxisManager.AxisBounds("Bottom Axis", -1.275, false, -0.041, false), new AxisManager.AxisBounds("Left Axis", 3.83, false, 0.244, false))));
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	//String currentName = simulation_0.getPresentationName();
	xYPlot_1.encode(resolvePath("/"+AbsPath+"/"+currentName+".post/SuperScene/"+"99_AccF_plot.png"), "png", 2560, 1440);

	getSimulation().println("create Plots ---- DONE");
}


private String[] getReportValue() {
	Simulation simulation_0 = getActiveSimulation();
	//get frontalAreaReport value
	FrontalAreaReport frontalAreaReport_0 = ((FrontalAreaReport) simulation_0.getReportManager().getReport("FrontalArea"));
	double dareaReport = frontalAreaReport_0.getValue();
	dareaReport = dareaReport * 2;
	String areaReport = String.format("%.3f", dareaReport);
	//get forceCoefficientReport value
	ForceCoefficientReport forceCoefficientReport_0 = ((ForceCoefficientReport) simulation_0.getReportManager().getReport("Cd"));
	double[] dforcecd = forceCoefficientReport_0.getValue().toArray();
	String forcecd = String.format("%.3f", dforcecd[0]);
	double CdA = dareaReport * dforcecd[0];
	String forceCdA = String.format("%.3f", CdA);
	String[] reportValue = {areaReport, forcecd, forceCdA};
	getSimulation().println("---get Report value ok---");
	return reportValue;
  }


private void outValue(String[] reportValue)throws IOException{
	Simulation simulation_0 = getActiveSimulation();
	String presentationName = simulation_0.getPresentationName();
	String AbsPath = simulation_0.getSessionDirFile().getAbsolutePath();
	String ValuePath = (AbsPath+"/"+presentationName+".post/Value/Value");
	String CdValue = reportValue[0];
	String AeraValue = reportValue[1];
	String CdA = reportValue[2];
	File f = new File(ValuePath);
	if(!f.exists()){
    //先得到文件的上级目录，并创建上级目录，在创建文件
    f.getParentFile().mkdir();
	f.createNewFile();}
    try {
        //创建文件
		FileOutputStream fop = new FileOutputStream(f);
		// 构建FileOutputStream对象,文件不存在会自动新建
		OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
		// 构建OutputStreamWriter对象,参数可以指定编码,默认为操作系统默认编码,windows上是gbk
		writer.append(CdValue);
		// 写入到缓冲区
		writer.append("\r\n");
		// 换行
		writer.append(AeraValue);
		writer.append("\r\n");
		writer.append(CdA);
		// 刷新缓存冲,写入到文件,如果下面已经没有写入的内容了,直接close也会写入
		writer.close();
		// 关闭写入流,同时会把缓冲区内容写入文件,所以上面的注释掉
		fop.close();
		// 关闭输出流,释放系统资源
    } catch (IOException ex) {
	getSimulation().println(ex);}
	getSimulation().println("---output Report value ok---");
}





////*************from zitong Supre view calculation
//set view
//import scene
//output widthAspectRatio
private double set_view(Vector vBoundary, String ViewNormStr, String ViewUpStr, double ViewMargin, Scene scene_0)
{
	CurrentView SceneView = scene_0.getCurrentView();
	// calculating the view vectors, ViewNorm: focal point to view point
	double[] ViewNorm = getViewVec(ViewNormStr);
	double[] ViewUp = getViewVec(ViewUpStr);
	double[] ViewRight = NormalizedCrossProduct(ViewUp, ViewNorm);
	ViewUp = NormalizedCrossProduct(ViewNorm, ViewRight);
	
	Simulation simulation_0 = getActiveSimulation();
	
	// create global vector parameters to calculate the projected limits of vBoundary
	simulation_0.get(GlobalParameterManager.class).createGlobalParameter(VectorGlobalParameter.class, "vViewUp");

	VectorGlobalParameter vectorGlobalParameter_0 = 
		((VectorGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("vViewUp"));

	vectorGlobalParameter_0.getQuantity().setComponents(ViewUp[0], ViewUp[1], ViewUp[2]);

	simulation_0.get(GlobalParameterManager.class).createGlobalParameter(VectorGlobalParameter.class, "vViewRight");

	VectorGlobalParameter vectorGlobalParameter_1 = 
		((VectorGlobalParameter) simulation_0.get(GlobalParameterManager.class).getObject("vViewRight"));

	vectorGlobalParameter_1.getQuantity().setComponents(ViewRight[0], ViewRight[1], ViewRight[2]);

	// create user field functions to calculate the projected limits of vBoundary
	UserFieldFunction userFieldFunction_0 = 
		simulation_0.getFieldFunctionManager().createFieldFunction();

	userFieldFunction_0.getTypeOption().setSelected(FieldFunctionTypeOption.Type.VECTOR);

	userFieldFunction_0.setDimensionsVector(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

	userFieldFunction_0.setDefinition("[$${Position}[0], -$${Position}[1], $${Position}[2]]");

	userFieldFunction_0.setFunctionName("Position_Mirror");

	userFieldFunction_0.setPresentationName("Position_Mirror");

	UserFieldFunction userFieldFunction_1 = 
		simulation_0.getFieldFunctionManager().createFieldFunction();

	userFieldFunction_1.getTypeOption().setSelected(FieldFunctionTypeOption.Type.SCALAR);

	userFieldFunction_1.setDimensionsVector(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

	userFieldFunction_1.setFunctionName("ViewDimension");

	userFieldFunction_1.setPresentationName("ViewDimension");

	// calculate the projected limits of vBoundary
	double[] ViewLimit = new double[4];	// left, right, bottom, top
	
	MaxReport maxReport_0 = simulation_0.getReportManager().createReport(MaxReport.class);

	MinReport minReport_0 = simulation_0.getReportManager().createReport(MinReport.class);

	minReport_0.getParts().setObjects(vBoundary);

	maxReport_0.getParts().setObjects(vBoundary);

	minReport_0.setFieldFunction(userFieldFunction_1);

	maxReport_0.setFieldFunction(userFieldFunction_1);

	userFieldFunction_1.setDefinition("dot($${vViewRight}, $${Position})");

	ViewLimit[0] = minReport_0.getValue();

	ViewLimit[1] = maxReport_0.getValue();

	userFieldFunction_1.setDefinition("dot($${vViewRight}, $${Position_Mirror})");

	ViewLimit[0] = Math.min(minReport_0.getValue(), ViewLimit[0]);

	ViewLimit[1] = Math.max(maxReport_0.getValue(), ViewLimit[1]);

	userFieldFunction_1.setDefinition("dot($${vViewUp}, $${Position})");

	ViewLimit[2] = minReport_0.getValue();

	ViewLimit[3] = maxReport_0.getValue();

	userFieldFunction_1.setDefinition("dot($${vViewUp}, $${Position_Mirror})");

	ViewLimit[2] = Math.min(minReport_0.getValue(), ViewLimit[2]);

	ViewLimit[3] = Math.max(maxReport_0.getValue(), ViewLimit[3]);

	// create a local CSYS where e_x = ViewRight, e_y = ViewUp, e_z = ViewNorm
	Units units_0 = 
		simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

	LabCoordinateSystem labCoordinateSystem_0 = 
		simulation_0.getCoordinateSystemManager().getLabCoordinateSystem();

	CartesianCoordinateSystem cartesianCoordinateSystem_0 = 
		labCoordinateSystem_0.getLocalCoordinateSystemManager().createLocalCoordinateSystem(CartesianCoordinateSystem.class, "Cartesian");

	Coordinate coordinate_0 = 
		cartesianCoordinateSystem_0.getOrigin();

	coordinate_0.setValue(new DoubleVector(new double[] {0.0, 0.0, 0.0}));

	cartesianCoordinateSystem_0.setBasis0(new DoubleVector(ViewRight));

	cartesianCoordinateSystem_0.setBasis1(new DoubleVector(ViewUp));

	cartesianCoordinateSystem_0.setPresentationName("CurrentViewCSYS");

	// set view
	SceneView.setInput(new DoubleVector(new double[] {0.0, 0.0, 0.0}), new DoubleVector(new double[] {-1.0, 0.0, 0.0}), new DoubleVector(new double[] {0.0, 0.00, 1.0}), 1.0, 1);

	SceneView.setCoordinateSystem(cartesianCoordinateSystem_0);

	Coordinate coordinate_1 = SceneView.getViewUpCoordinate();

	coordinate_1.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] {0.0, 1.0, 0.0}));

	Coordinate coordinate_2 = SceneView.getFocalPointCoordinate();

	coordinate_2.setCoordinate(units_0, units_0, units_0, 
		new DoubleVector(new double[] { 0.5*(ViewLimit[0]+ViewLimit[1]), 0.5*(ViewLimit[2]+ViewLimit[3]), 0.0}));

	Coordinate coordinate_3 = SceneView.getPositionCoordinate();

	coordinate_3.setCoordinate(units_0, units_0, units_0, 
		new DoubleVector(new double[] { 0.5*(ViewLimit[0]+ViewLimit[1]), 0.5*(ViewLimit[2]+ViewLimit[3]), 1.0}));

	ParallelScale parallelScale_0 = SceneView.getParallelScale();

	parallelScale_0.setValue(5e-4*(ViewLimit[3]-ViewLimit[2])*(1+ViewMargin*2));	// scale = physical height / [2(m)]

	//simulation_0.println(""+ViewLimit[0]+", "+ViewLimit[1]+", "+ViewLimit[2]+", "+ViewLimit[3]+";");

	double AspectRatio = (ViewLimit[1]-ViewLimit[0]) / (ViewLimit[3]-ViewLimit[2]);

	// tidy up
	SceneView.setCoordinateSystem(labCoordinateSystem_0);

	simulation_0.getReportManager().removeObjects(minReport_0, maxReport_0);

	simulation_0.getFieldFunctionManager().removeObjects(userFieldFunction_1);	// delete position_mirror fcn

	simulation_0.getFieldFunctionManager().removeObjects(userFieldFunction_0);

	simulation_0.get(GlobalParameterManager.class).remove(vectorGlobalParameter_0);

	simulation_0.get(GlobalParameterManager.class).remove(vectorGlobalParameter_1);

	labCoordinateSystem_0.getLocalCoordinateSystemManager().remove(cartesianCoordinateSystem_0);

	return AspectRatio;
}

private double [] getViewVec(String ViewStr)
{
	double[] view_vec = { 0.0, 0.0, 0.0};
	switch (ViewStr)
	{
		case "X":
		case "+X":
			view_vec[0] = 1.0;
			break;
		case "-X":
			view_vec[0] =-1.0;
			break;
		case "Y":
		case "+Y":
			view_vec[1] = 1.0;
			break;
		case "-Y":
			view_vec[1] =-1.0;
			break;
		case "Z":
		case "+Z":
			view_vec[2] = 1.0;
			break;
		case "-Z":
			view_vec[2] =-1.0;
			break;
		case "ISO":
		case "45":
			view_vec[0] =-1.0; view_vec[1] =-1.0; view_vec[2] = 1.0;
			break;
		default:
			String[] view_vec_split = ViewStr.split(",");
			if (ViewStr.length() < 3)
			{
				throw new IllegalArgumentException("Invalid view specification: " + ViewStr);
			}
			else
			{
				double vec_norm = 0.0;
				for (int i = 0 ;i < 3; i++)
				{
					view_vec[i] = Double.parseDouble(view_vec_split[i]);
					vec_norm = vec_norm + view_vec[i] * view_vec[i];
				}
				vec_norm = Math.sqrt(vec_norm);
				for (int i = 0 ;i < 3; i++)
				{
					view_vec[i] = view_vec[i] / vec_norm;
				}
			}
	}
	return view_vec;
}

private double [] NormalizedCrossProduct(double[] vA, double[] vB)
{
	double[] vProduct = new double[3];
	vProduct[0] = vA[1] * vB[2] - vA[2] * vB[1];
	vProduct[1] = vA[2] * vB[0] - vA[0] * vB[2];
	vProduct[2] = vA[0] * vB[1] - vA[1] * vB[0];
	double norm = 0.0;
	for (int i = 0; i<3; i++)
	{
		norm = norm + vProduct[i]*vProduct[i];
	}
	norm = Math.sqrt(norm);
	for (int i = 0; i<3; i++)
	{
		vProduct[i] = vProduct[i]/norm;
	}
	return vProduct;
}



/////////end///////////

}