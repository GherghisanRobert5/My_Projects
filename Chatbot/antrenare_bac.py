import random
import json
import pickle
import numpy as np
import nltk
import tensorflow as tf
from nltk.stem import WordNetLemmatizer


lem = WordNetLemmatizer()

optiuni=json.loads(open('optiuni.json').read())

bac_cuvinte=[]
bac_categori=[]
bac_documente=[]

lit_ign = ['?','!',".",","]

for optiune in optiuni["optiuni_bac"]:
    for intreb in optiune["intrebare"]:
        lista_cuv = nltk.word_tokenize(intreb.lower())
        bac_cuvinte.extend(lista_cuv)
        bac_documente.append((lista_cuv,optiune['subiect']))
        if optiune['subiect'] not in bac_categori:
            bac_categori.append(optiune['subiect'])

bac_cuvinte=sorted(set(bac_cuvinte))
bac_categori=sorted(set(bac_categori))

pickle.dump(bac_cuvinte, open('bac_cuvinte.pkl','wb'))
pickle.dump(bac_categori, open('bac_categori.pkl','wb'))

antrenament =[]
out_gol = [0] * len(bac_categori)
for document in bac_documente:
    bow = []
    categ_cuv = document[0]
    categ_cuv =[lem.lemmatize(cuvant.lower()) for cuvant in categ_cuv]

    for cuvant in bac_cuvinte:
        if cuvant in categ_cuv:

            bow.append(1)
        else:
            bow.append(0)
    linii=list(out_gol)
    linii[bac_categori.index(document[1])]=1
    antrenament.append(bow+linii)

random.shuffle(antrenament)
antrenament=np.array(antrenament)

x_antrenament = antrenament[:, :len(bac_cuvinte)]
y_antrenament = antrenament[:, len(bac_cuvinte):]

model2 = tf.keras.Sequential()
model2.add(tf.keras.layers.Dense(128, input_shape=(len(x_antrenament[0]),), activation='relu'))
model2.add(tf.keras.layers.Dropout(0.5))
model2.add(tf.keras.layers.Dense(64, activation='relu'))
model2.add(tf.keras.layers.Dropout(0.5))
model2.add(tf.keras.layers.Dense(len(y_antrenament[0]), activation='softmax'))

sgd1 = tf.keras.optimizers.SGD(learning_rate=0.01, momentum=0.9, nesterov=True)


model2.compile(loss='categorical_crossentropy', optimizer=sgd1, metrics=['accuracy'])

b=model2.fit(x_antrenament, y_antrenament, epochs=200, batch_size=5, verbose=1)
model2.save('model2_bun.h5',b)
model2.summary()

