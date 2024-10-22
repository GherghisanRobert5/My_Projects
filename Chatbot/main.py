import random
import json
import pickle
import re
import string
import matplotlib.pyplot as plt
from matplotlib import pyplot as plt
import numpy as np
import nltk
import tensorflow as tf
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords

import pandas as pd
from tensorflow.python.keras.models import load_model
lem = WordNetLemmatizer()

optiuni=json.loads(open('optiuni.json').read())

cuvinte = pickle.load(open('cuvinte.pkl','rb'))
categori = pickle.load(open('categori.pkl','rb'))
bac_cuvinte = pickle.load(open('bac_cuvinte.pkl','rb'))
bac_categori = pickle.load(open('bac_categori.pkl','rb'))

model1=tf.keras.models.load_model('model1_bun.h5')
model2=tf.keras.models.load_model('model2_bun.h5')


def cele_mai_dese_cuvinte(input):
    a=0
    cuv=[]
    cuv1=[]
    freq=[]
    freq1 = []
    input=input.translate(str.maketrans(string.punctuation, ' '*len(string.punctuation)))
    input=nltk.word_tokenize(input)
    for i in input:
        cuv.append(lem.lemmatize(i))

    for i in cuv:
        if i not in cuv1 and i not in stopwords.words('romanian') and i not in stopwords.words('english'):
            cuv1.append(i)
    for i in cuv1:
        a=0
        for j in cuv:
            if i==j:
                a=a+1
        freq.append({'frecventa': a, 'cuvant': i})
        freq1.append(a)

    freq=sorted(freq, key = lambda freq: (freq['frecventa']),reverse=True)
    return freq

def check_math(prop):
    if prop.isupper()==0 or prop.islower()==0:
        return 1
    else:
        return 0
def math(x:list[str]):
    v: list[str] = []
    d:int=0
    y:str
    a:int =0
    b:int =0
    while "(" in x:
        i = 0
        for j in range(0, len(x)):
            if x[j] == ")":
                b = j
                break
        for j in range(b, -1, -1):
            if x[j] == "(":
                a = j;
                break
        for j in range(a+1, b):
            v.append(x[j])
            i = i + 1
        y=math(v)
        d=b-a

        while d>0:
            x.pop(a)
            d=d-1
        x[a]=y
        break
    if "(" in x:
        math(x)
    while "^" in x:
        for i in range(0, len(x)):
            if x[i] == "^":
                r = pow(float(x[i - 1]), float(x[i + 1]))
                x[i - 1] = str(r)
                x.pop(i)
                x.pop(i)
                break
    while "*" in x or "/" in x:
        for i in range(0, len(x)):
            if x[i] == "*" or x[i] == "/":
                if x[i] == "*":
                    r = float(x[i - 1]) * float(x[i + 1])
                    x[i - 1] = str(r)
                    x.pop(i)
                    x.pop(i)
                    break
                if x[i] == "/":
                    r = float(x[i - 1]) / float(x[i + 1])
                    x[i - 1] = str(r)
                    x.pop(i)
                    x.pop(i)
                    break
    while "+" in x or "-" in x:
        for i in range(len(x)):
            if x[i] == "+" or x[i] == "-":
                if x[i] == "+":
                    r = float(x[i - 1]) + float(x[i + 1])
                    x[i - 1] = str(r)
                    x.pop(i)
                    x.pop(i)
                    break
                if x[i] == "-":
                    r = float(x[i - 1]) - float(x[i + 1])
                    x[i - 1] = str(r)
                    x.pop(i)
                    x.pop(i)
                    break

    return x[len(x)-1]


def curatare(prop):
    cuv_prop = nltk.word_tokenize(prop.lower())
    cuv_prop = [lem.lemmatize(cuvant) for cuvant in cuv_prop]
    return cuv_prop

def bow(prop):
    cuv_prop=curatare(prop)
    bow=[0]*len(cuvinte)
    for cuv in cuv_prop:
        for i,cuvant in enumerate(cuvinte):
            if cuvant == cuv:
                bow[i]=1
    return np.array(bow)

def bow_bac(prop):
    cuv_prop=curatare(prop)
    bow=[0]*len(bac_cuvinte)
    for cuv in cuv_prop:
        for i,cuvant in enumerate(bac_cuvinte):
            if cuvant == cuv:
                bow[i]=1
    return np.array(bow)

def cea_mai_buna_alegere(prop):
    bow1 = bow(prop)
    pred = model1.predict(np.array([bow1]))[0]
    err=0.1
    rez=[[i,r]for i, r in enumerate(pred) if r>err]
    rez.sort(key=lambda x:x[1],reverse=True)
    lista_fin=[]
    for r in rez:
        lista_fin.append({'optiunea': categori[r[0]], 'Probabilitate': str(r[1])})
    return lista_fin
def cea_mai_buna_alegere_bac(prop):
    bow1 = bow_bac(prop)
    pred = model2.predict(np.array([bow1]))[0]
    err=0.3
    rez=[[i,r]for i, r in enumerate(pred) if r>err]
    rez.sort(key=lambda x:x[1],reverse=True)
    lista_fin=[]
    for r in rez:
        lista_fin.append(bac_categori[r[0]])
    return lista_fin

def raspuns(lista_optiuni,json_optiuni,input,art,doc):
    yy=[]
    y_bac1=[]
    categorie = lista_optiuni[0]['optiunea']
    opt_lista=json_optiuni['optiuni']
    if categorie == 'article':
        k = cele_mai_dese_cuvinte(art)
        print(k)
        x = []
        y = []
        aa=[]
        bb=[]
        y_bac=[]
        g = 1
        for i in k:
            x.append(i['frecventa'])
            g = g + 1
            if g == 20:
                break
        g = 1
        for i in k:
            y.append(i['cuvant'])
            g = g + 1
            if g == 20:
                break

        for i in k:
            if cea_mai_buna_alegere_bac(i['cuvant']):
                y_bac.append({'cuvant':cea_mai_buna_alegere_bac(i['cuvant'])[0],'frecventa':i['frecventa']})
        '''for i in y:
            if cea_mai_buna_alegere_bac(i):
                y_bac.append(cea_mai_buna_alegere_bac(i)[0])
        '''
        print("y_bac",y_bac)

        lst_bac=json_optiuni['optiuni_bac']
        print("lst_bac",lst_bac)
        for ya in y_bac:
            for i in lst_bac:
                if i['subiect']==ya['cuvant']:
                    yy.append({"nume":i['raspunsuri'][0],"frecventa":ya['frecventa']})
        g=1
        for i in yy:
            aa.append(i['frecventa'])
            g = g + 1
            if g == 17:
                break

        g = 1
        for i in yy:
            bb.append(i['nume'])
            g = g + 1
            if g == 17:
                break

        plt.bar(bb,aa,color='orange')
        plt.style.use('fivethirtyeight')
        plt.xticks(bb, rotation=90)
        plt.show()
    if categorie == 'frequency':
        f = cele_mai_dese_cuvinte(doc)
        x1 = []
        y1 = []
        g = 1
        for i in f:
            x1.append(i['frecventa'])
            g = g + 1
            if g == 20:
                break
        g = 1
        for i in f:
            y1.append(i['cuvant'])
            g = g + 1
            if g == 20:
                break
        plt.bar(y1, x1)
        plt.style.use('fivethirtyeight')
        plt.xticks(y1, rotation=90)
        plt.show()
    for i in opt_lista:
        if i['subiect']==categorie:
            rasp=random.choice(i['raspunsuri'])



    return rasp
b=1
while b==1:
    u_input= input("<user> ")
    u_input= u_input.lower()
    with open('article.txt', 'r') as file:
        art = file.read().replace('\n', '')
    with open('docfreq.txt', 'r') as file:
        doc = file.read().replace('\n', '')
    art=art.lower()
    doc=doc.lower()


    a=cea_mai_buna_alegere(u_input)
    print(f"<PyPy> {raspuns(a,optiuni,u_input,art,doc)}")
    if check_math(u_input):
        print(math(u_input.split()))

    if a[0]['optiunea']=='conversation_end':
        b=0

