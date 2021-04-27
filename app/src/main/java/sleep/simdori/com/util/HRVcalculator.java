package sleep.simdori.com.util;

import android.icu.util.TimeUnit;
import android.util.Log;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.N;

public class HRVcalculator {
    List<Double> peaks;
    List<Double> nnis;
    double nni_avg=0;
    double rmssd=0;
    double sdsd=0;
    double sdnn=0;
    double stress=0;
    int lf=0;
    int hf=0;
    int nn50;
    int pnn50;
    int nn20;
    int pnn20;
    int median_nn;
    int range_nn;
    int cvsd;
    int cv_nni;
    int mean_hr;
    int max_hr;
    int min_hr;
    int std_hr;

    public HRVcalculator(List<Double> peaks){
        this.peaks=peaks;
        setNNI(peaks);
        setNNIAVG();
        setRMSSD();
        setSDNN();
        setSDSD();
        setStress();
    }

    public void setNNI(List<Double> peaks){
        nnis = new ArrayList<Double>();
        for(int i=0;i<peaks.size()-1;i++){
            nnis.add(peaks.get(i+1) - peaks.get(i));
        }
    }
    public void setNNIAVG(){
        double nni_sum=0;
        for(double nni : nnis){
            nni_sum += nni;
        }
        nni_avg = nni_sum/nnis.size();
    }
//    인접한 NN 간격 사이의 차이 제곱 합 평균의 제곱근
    public void setRMSSD(){
        double nni_pow_sum=0;
        for(int i=0;i<nnis.size()-1;i++){
            nni_pow_sum += Math.pow(nnis.get(i+1)-nnis.get(i),2);
        }
        rmssd = Math.sqrt(nni_pow_sum/nnis.size());
    }
//    인접한 NN 간격 사이 차이의 표준 편차
    public void setSDSD(){
        double dsum=0;
        double nni_diff_avg = 0;
        List<Double> nni_diff= new ArrayList<>();
        for(int i=0;i<nnis.size()-1;i++){
            nni_diff.add(nnis.get(i+1)-nnis.get(i));
            nni_diff_avg += nni_diff.get(i);
        }
        nni_diff_avg /= (nni_diff.size());
        for(int i=0;i<nni_diff.size();i++){
            dsum += (nni_diff.get(i) - nni_diff_avg)*(nni_diff.get(i) - nni_diff_avg);
        }
        sdsd = Math.sqrt(dsum/nni_diff.size());
    }
//    LF 혹은 HF 수치 (nu) = (LF 혹은 HF (ms2))*100/ (전체 파워 (ms2) – VLF (ms2)), 미완성
    public void setStress(){

        double y[] = new double[nnis.size()]; //Imaginary Part
        for(int i=0;i<nnis.size();i++){
            y[i] = 0;
        }

        double x[] = new double[nnis.size()]; //Real Part
        for(int i=0;i<nnis.size();i++){
            x[i] = nnis.get(i);
        }

        double[] a = new double[2*nnis.size()]; //fft 수행할 배열 사이즈 2N
        for(int k=0;k<nnis.size();k++){
            a[2*k] = x[k];   //Re
            a[2*k+1] = y[k]; //Im
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(nnis.size()); //1차원의 fft 수행
        fft.complexForward(a); //a 배열에 output overwrite

        double[] mag = new double[nnis.size()/2];
        System.out.print("mag : ");
        for(int k=0;k<nnis.size()/2;k++){
            mag[k] = Math.sqrt(Math.pow(a[2*k],2)+Math.pow(a[2*k+1],2))/nnis.size();
            if (mag[k]<0.15 && mag[k]>0.04) {
                lf++;
            }else if(mag[k]>0.15&&mag[k]<0.4){
                hf++;
            }
            System.out.print(mag[k]+", ");
        }
        System.out.println("");
        stress = (double)lf/(double)hf;

    }
//    모든 NN 간격의 표준 편차
    public void setSDNN(){
        double dsum=0;
        for(int i=0;i<nnis.size()-1;i++){
            dsum += (nnis.get(i) - nni_avg)*(nnis.get(i) - nni_avg);
        }
        sdnn = Math.sqrt(dsum/nnis.size());
    }
//===============================여기서부터 만드러야함
    public void setNN50(){ }
    public void setpNN50(){ }
    public void setNN20(){ }
    public void setpNN20(){ }
    public void setMedian_NN(){ }
    public void setRange_NN(){ }
    public void setCVSD(){ }
    public void setCV_NNI(){ }
    public void setMean_HR(){ }
    public void setMax_HR(){ }
    public void setMin_HR(){ }
    public void setSTD_HR(){ }


    public List<Double> getPeaks() {
        return peaks;
    }
    public List<Double> getNNI() {
        return nnis;
    }
    public double getNNIAVG() {
        return nni_avg;
    }
    public double getRMSSD() {
        return rmssd;
    }
    public double getSDSD() {
        return sdsd;
    }
    public double getSDNN() {
        return sdnn;
    }
    public double getStress() {
        return stress;
    }
    public double getNN50() {
        return nn50;
    }
    public double getpNN50() {
        return pnn50;
    }
    public double getNN20() {
        return nn20;
    }
    public double getpNN20() {
        return pnn20;
    }
    public double getMedian_NN() {
        return median_nn;
    }
    public double getRange_NN() {
        return range_nn;
    }
    public double getCVSD() {
        return cvsd;
    }
    public double getCV_NNI() {
        return cv_nni;
    }
    public double getMean_HR() {
        return mean_hr;
    }
    public double getMax_HR() {
        return max_hr;
    }
    public double getMin_HR() {
        return min_hr;
    }
    public double getSTD_HR() {
        return std_hr;
    }


}
