//Alistair Dobke and Mark Mann
//Matrix for math OCR project
//http://cs.hmc.edu/~adobke/nn/

package com.example.math_ocr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;



public class Matrix {

	double[][] mat;
	
	public Matrix(double[][] mat) {
		this.mat = mat;
	}
	
	public Matrix(double[] mat, boolean row) {
		if(row) {
			this.mat = new double[1][mat.length];
			this.mat[0] = mat;
		} else {
			this.mat = new double[mat.length][1];
			for(int i = 0; i < mat.length; i++) {
				this.mat[i][0] = mat[i];
			}
		}
	}
	
	/**Constructs a matrix from the given file, assuming that it is csv.
	 * @throws IOException */
	public Matrix(String filename, int height, int width) throws IOException {
		this.mat = new double[height][width];
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(reader == null) {
			// bad stuff....
			return;
		}
		
		String matrix = reader.readLine();
		
		String[] strMat = matrix.split(",", 0);
		int index = 0;
		for(int i = 0; i < height; i++) {
			index = i * width;
			for(int j = 0; j < width; j++) {
				mat[i][j] = Double.parseDouble(strMat[index + j]);
			}
		}
	}
	
	public static Matrix multiplyMatrices(Matrix A, Matrix B) {
		int aRows = A.getHeight();
		int aCols = A.getWidth();
		int bRows = B.getHeight();
		int bCols = B.getWidth();
		
		// Cannot multiply matrices whose dimensions do not match.
		if(aCols != bRows) {
			return null;
		}
		
		double[][] newMat = new double[aRows][bCols];

		for(int i = 0; i < aRows; i++) {
			for(int j = 0; j < bCols; j++) {
				for(int k = 0; k < aCols; k++) {
					newMat[i][j] += A.get(i, k) * B.get(k, j);
				}
			}
		}

		return new Matrix(newMat);
	}
	
	/**Multiplies two matrices together and then will limit them 
	 * based on the logsig transfer function.*/
	public static Matrix multiplyMatricesWithLogsig(Matrix A, Matrix B) {
		int aRows = A.getHeight();
		int aCols = A.getWidth();
		int bRows = B.getHeight();
		int bCols = B.getWidth();
		
		// Cannot multiply matrices whose dimensions do not match.
		if(aCols != bRows) {
			return null;
		}
		
		double[][] newMat = new double[aRows][bCols];

		for(int i = 0; i < aRows; i++) {
			for(int j = 0; j < bCols; j++) {
				for(int k = 0; k < aCols; k++) {
					newMat[i][j] += A.get(i, k) * B.get(k, j);
				}
				newMat[i][j] = 1.0/(1 + Math.exp(-1 * newMat[i][j]));
			}
		}

		return new Matrix(newMat);
	}
	
	/**Multiplies two matrices together and then adds the bias matrix in and then logsigs them all.*/
	public static Matrix multiplyLogsigWithBias(Matrix A, Matrix B, Matrix Bias) {
		int aRows = A.getHeight();
		int aCols = A.getWidth();
		int bRows = B.getHeight();
		int bCols = B.getWidth();
		int biasRows = Bias.getHeight();
		int biasCols = Bias.getWidth();
		
		// Cannot multiply matrices whose dimensions do not match.
		// We also need the bias to have the same dimension as the new matrix.
		if(aCols != bRows){
			System.out.println("A does not have the same number of columns as B does rows");
			System.out.println("A Number of cols: " + A.getWidth());
			System.out.println("B Number of rows: " + B.getHeight());
			return null;
		}
		
		if(aRows != biasRows) {
			System.out.println("Bias does not have the same number of rows as A");
			return null;
		}
		
		if(bCols != biasCols) {
			System.out.println("Bias does not have the same number of cols as B");
			return null;
		}
		
		double[][] newMat = new double[aRows][bCols];

		for(int i = 0; i < aRows; i++) {
			for(int j = 0; j < bCols; j++) {
				for(int k = 0; k < aCols; k++) {
					newMat[i][j] += A.get(i, k) * B.get(k, j);
				}
				newMat[i][j] += Bias.get(i, j);
				newMat[i][j] = 1.0/(1 + Math.exp(-1 * newMat[i][j]));
			}
		}

		return new Matrix(newMat);
	}
	
	public static Matrix multiplyAddBias(Matrix A, Matrix B, Matrix Bias) {
		int aRows = A.getHeight();
		int aCols = A.getWidth();
		int bRows = B.getHeight();
		int bCols = B.getWidth();
		int biasRows = Bias.getHeight();
		int biasCols = Bias.getWidth();
		
		// Cannot multiply matrices whose dimensions do not match.
		// We also need the bias to have the same dimension as the new matrix.
		if(aCols != bRows || aRows != biasRows || bCols != biasCols) {
			return null;
		}
		
		double[][] newMat = new double[aRows][bCols];

		for(int i = 0; i < aRows; i++) {
			for(int j = 0; j < bCols; j++) {
				for(int k = 0; k < aCols; k++) {
					newMat[i][j] += A.get(i, k) * B.get(k, j);
				}
				newMat[i][j] += Bias.get(i, j);
			}
		}

		return new Matrix(newMat);
	}
	
	/**Takes the logistic sigmoid of this Matrix*/
	public void logsig() {
		for(int i = 0; i < this.mat.length; i++) {
			for(int j = 0; j < this.mat[0].length; j++) {
				this.mat[i][j] = 1.0/(1 + Math.exp(-1 * mat[i][j]));
			}
		}
	}
	
	/**Takes the hyperbolic tangent of each element in this matrix*/
	public void tansig() {
		for(int i = 0; i < this.mat.length; i++) {
			for(int j = 0; j < this.mat[0].length; j++) {
				this.mat[i][j] = (2.0/(1 + Math.exp(-2 * this.mat[i][j]))) - 1;
			}
		}
	}
	
	public double get(int i, int j) {
		return this.mat[i][j];
	}
	
	public int getHeight() {
		return this.mat.length;
	}
	
	public int getWidth() {
		return this.mat[0].length;
	}
}





