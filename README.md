## Bangla-Scene-Text-Collector

This is the official repository of the paper  **"A crowdsource based framework for Bengali scene text data collection and detection"** ([link](https://doi.org/10.1016/j.compeleceng.2023.109025)).
`SceneTextCollector` is the Android application that was proposed in the paper. The app was developed in Android Studio and written in Kotlin.
`scene_text_tools` contains the scripts for data downloading, processing, and training along with all the data used in the study.

The YOLOv5 model that was used in our paper was taken from  the following [repository](https://github.com/ultralytics/yolov5).

### Development Setup:
`OS: Windows 10 Pro, Kubuntu 22.04 LTS`
`Android Studio: Chipmunk (2021.2.1)`
`Python: 3.9`

# **A crowdsource based framework for Bengali scene text data collection and detection**
> Md Yearat Hossain, Tanzilur Rahman

## Abstract

Scene texts are the type of texts that we see in our surroundings when we are outside. They have custom fonts, sizes, shapes and colors that are often different from the fonts or styles typically found in documents. 
Scene text recognition has grown in popularity due to its significance in the development of smarter automated systems. 
Despite the fact that numerous types of research on scene text detection and recognition have been conducted in different languages, similar work on the Bengali language is limited by the lack of large-scale usable datasets.
In this paper, we propose a multipurpose system for Bengali Scene text that allows for easy dataset collection via crowdsourcing as well as annotation, classification and detection tasks to be performed on the same platform.
It comes with an Android-based application that can be used to capture or load images from the device, select Bengali texts over the images and annotate them. The images and labels are stored in the cloud. 
A Python-based script is used to perform real-time data processing, analysis, and detection tasks. The proposed system has also been tested on a crowdsourced Bengali scene text dataset collected by this system. 
The classification model achieved an accuracy score of 97%, and the detection model achieved a Mean Average Precision score of 92 on the on these crowd contributed dataset.

## Cite
```
@article{hossain2023crowdsource,
  title={A crowdsource based framework for Bengali scene text data collection and detection},
  author={Hossain, Md Yearat and Rahman, Tanzilur},
  journal={Computers and Electrical Engineering},
  volume={112},
  pages={109025},
  year={2023},
  publisher={Elsevier}
}
```
