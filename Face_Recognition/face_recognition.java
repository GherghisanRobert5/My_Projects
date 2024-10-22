package com.anlak.opencv;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.*;

public class main {
	
	public static void run() {
		CascadeClassifier upperbodyDetector= new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_upperbody.xml");
		CascadeClassifier faceDetector= new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_frontalface_alt.xml");
		CascadeClassifier smileDetector= new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_smile.xml");
		CascadeClassifier lowerbodyDetector= new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_lowerbody.xml");
		//CascadeClassifier eyesDetector = new CascadeClassifier("C:\\\\Users\\\\ser1\\\\Downloads\\\\opencv\\\\sources\\\\data\\\\haarcascades_cuda\\haarcascade_eye_tree_eyeglasses.xml");
		
		CascadeClassifier lefteyeDetector = new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_lefteye_2splits.xml");
		CascadeClassifier righteyeDetector = new CascadeClassifier("C:\\Users\\ser1\\Downloads\\opencv\\sources\\data\\haarcascades_cuda\\haarcascade_righteye_2splits.xml");
		Mat image = Imgcodecs.imread("man3.jpg");
		MatOfRect lefteye = new MatOfRect();
		MatOfRect righteye = new MatOfRect();
		MatOfRect fullbody = new MatOfRect();
		MatOfRect upperbody = new MatOfRect();
		MatOfRect lowerbody = new MatOfRect();
		MatOfRect faces = new MatOfRect();
		MatOfRect smile = new MatOfRect();
		MatOfRect eyes = new MatOfRect();
		upperbodyDetector.detectMultiScale(image, fullbody);
		lowerbodyDetector.detectMultiScale(image, lowerbody);
		faceDetector.detectMultiScale(image, faces);
		smileDetector.detectMultiScale(image,smile);
		//eyesDetector.detectMultiScale(image,eyes);
		lefteyeDetector.detectMultiScale(image,lefteye);
		righteyeDetector.detectMultiScale(image,righteye);
		
		for (Rect rect:upperbody.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(0,0,255));
		}
		for (Rect rect:lowerbody.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(0,0,255));
		}
		for (Rect rect:lefteye.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(255,0,0));
		}
		for (Rect rect:righteye.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(0,0,255));
		}
		
		for (Rect rect:faces.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(255,0,0));
		}

		
		for (Rect rect:smile.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(0,255,0));
		}
		
		
		String output = "output.jpg";
		Imgcodecs.imwrite(output,image);
		
		for (Rect rect:lowerbody.toArray())
		{
			Imgproc.rectangle(image, new Point(rect.x, rect.y),
					new Point(rect.x+rect.width, rect.y+rect.height),
					new Scalar(255,255,0));
		}
		
		
	}
	
    public static void main (String[] args)
    {
        System.out.println("Hello world");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        run();
    }
}

