import argparse
import torch
import numpy as np
from torch import nn, optim
from torch.utils.data import DataLoader
from model import Model
from dataset import Dataset
from datetime import datetime
import sys
import traceback
import logging

if torch.cuda.is_available():
    torch.set_default_device('cuda')
if torch.backends.mps.is_available():
    torch.set_default_device('mps')

def predict(dataset, model, text, next_words=1):
    try:
        model.eval()

        words = ['']*100+text.split(' ')
        segment = 100
            
        state_h, state_c = model.init_state(segment)

        i = 0
        while True:
            x = torch.tensor([[dataset.word_to_index[w] for w in words[i:i+segment]]])
            y_pred, (state_h, state_c) = model(x, (state_h, state_c))

            last_word_logits = y_pred[0][-1]
            p = torch.nn.functional.softmax(last_word_logits / temperature, dim=0).detach().cpu().numpy()
            word_index = np.random.choice(len(last_word_logits), p=p)
            if i+segment >= len(words):
                words.append(dataset.index_to_word[word_index])
                if i >= next_words:
                    if dataset.index_to_word[word_index] == "****4":
                        break
            
            i = i+1

        return words
    except Exception as e:
        logging.error(traceback.format_exc())
        return ['']*100+["error"]

parser = argparse.ArgumentParser()
parser.add_argument('--max-epochs', type=int, default=1)
parser.add_argument('--batch-size', type=int, default=512)
parser.add_argument('--sequence-length', type=int, default=100)
args = parser.parse_args()

dataset = Dataset(args)

model = Model(dataset)
optimizer = optim.Adam(model.parameters(), lr=0.0005)

checkpoint = torch.load("model.pt", map_location=torch.device('cpu'))
model.load_state_dict(checkpoint['model_state_dict'])
optimizer.load_state_dict(checkpoint['optimizer_state_dict'])

temperature = 1

for line in sys.stdin:
    print(" ".join(predict(dataset, model, text=line.rstrip())[100:]))
