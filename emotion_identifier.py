# -*- coding: utf-8 -*-
"""Emotion Identifier.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1Ao1To22L1Q-mCJVoLSKw_psdl75__6tT

# Install Dependencies

---
"""

import sys


from deepface import DeepFace



"""# ML Code

---


"""



def detect_emotion():
  face_analysis = DeepFace.analyze(img_path = sys.argv[1],actions = ['emotion'])
  print(face_analysis[0]['dominant_emotion'])

"""# Trigger

---


"""

detect_emotion()