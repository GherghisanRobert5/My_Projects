import random
import json
import pickle
import numpy as np
import nltk
import tensorflow as tf
from nltk.stem import WordNetLemmatizer


lem = WordNetLemmatizer()

optiuni=json.loads(open('optiuni.json').read())

cuvinte= []
categori= []
documente= []

lit_ign = ['?','!',".",","]



for optiune in optiuni["optiuni"]:
    for intreb in optiune["intrebare"]:
        lista_cuv = nltk.word_tokenize(intreb.lower())
        cuvinte.extend(lista_cuv)
        documente.append((lista_cuv,optiune['subiect']))
        if optiune['subiect'] not in categori:
            categori.append(optiune['subiect'])

cuvinte = [lem.lemmatize(cuvant) for cuvant in cuvinte if cuvant not in lit_ign]
print(cuvinte)


cuvinte=sorted(set(cuvinte))
categori=sorted(set(categori))

pickle.dump(cuvinte, open('cuvinte.pkl','wb'))
pickle.dump(categori, open('categori.pkl','wb'))







antrenament =[]
out_gol = [0] * len(categori)
for document in documente:
    bow = []
    categ_cuv = document[0]
    categ_cuv =[lem.lemmatize(cuvant.lower()) for cuvant in categ_cuv]

    for cuvant in cuvinte:
        if cuvant in categ_cuv:

            bow.append(1)
        else:
            bow.append(0)
    linii=list(out_gol)
    linii[categori.index(document[1])]=1
    antrenament.append(bow+linii)

random.shuffle(antrenament)
antrenament=np.array(antrenament)

x_antrenament = antrenament[:, :len(cuvinte)]
y_antrenament = antrenament[:, len(cuvinte):]

model1 = tf.keras.Sequential()
model1.add(tf.keras.layers.Dense(128, input_shape=(len(x_antrenament[0]),), activation='relu'))
model1.add(tf.keras.layers.Dropout(0.5))
model1.add(tf.keras.layers.Dense(64, activation='relu'))
model1.add(tf.keras.layers.Dropout(0.5))
model1.add(tf.keras.layers.Dense(len(y_antrenament[0]), activation='softmax'))

sgd1 = tf.keras.optimizers.SGD(learning_rate=0.01, momentum=0.9, nesterov=True)
#adam1=tf.keras.optimizers.Adam(learning_rate=0.01)

model1.compile(loss='categorical_crossentropy', optimizer=sgd1, metrics=['accuracy'])

a=model1.fit(x_antrenament, y_antrenament, epochs=200, batch_size=5, verbose=1)
model1.save('model1_bun.h5',a)
model1.summary()


