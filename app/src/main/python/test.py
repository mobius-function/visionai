import cv2
from PIL import Image
import numpy as np
from os.path import dirname, join

def test(tvDistance, dis=0):
    # return f'Hello from Python! {num} times'
    # TextView.setText('Hello from Python!')

    dis += 100
    toPrint = "Distance: " + str(dis) + " CM"
    tvDistance.setText(toPrint)
    return dis

    filename = join(dirname(__file__), 'ref_image.jpg')
    img = Image.open(join(dirname(__file__), 'ref_image.jpg'))
    # ary = np.array(img)

    # # Split the three channels
    # r,g,b = np.split(ary,3,axis=2)
    # r=r.reshape(-1)
    # g=r.reshape(-1)
    # b=r.reshape(-1)

    # # Standard RGB to grayscale 
    # bitmap = list(map(lambda x: 0.299*x[0]+0.587*x[1]+0.114*x[2], 
    # zip(r,g,b)))
    # bitmap = np.array(bitmap).reshape([ary.shape[0], ary.shape[1]])
    # bitmap = np.dot((bitmap > 128).astype(float),255)
    # im = Image.fromarray(bitmap.astype(np.uint8))




    ##############################

    # ivCamera.setImageBitmap(bitmap)

    # Load bitmap image ref_image.jpg
    # bitmapImage = cv2.imread('ref_image.jpg')
    # ivCamera.setImageURI(bitmapImage)

face_dector_path = join(dirname(__file__), 'haarcascades\haarcascade_frontalface_default.xml')
print(face_dector_path)







