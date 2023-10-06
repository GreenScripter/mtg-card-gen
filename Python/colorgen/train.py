import argparse
import torch
import numpy as np
from torch import nn, optim
from torch.utils.data import DataLoader
from model import Model
from dataset import Dataset
from datetime import datetime

if torch.cuda.is_available():
    torch.set_default_device('cuda')
if torch.backends.mps.is_available():
    torch.set_default_device('mps')

def train(dataset, model, args):
    model.train()

    dataloader = DataLoader(dataset, batch_size=args.batch_size)
    criterion = nn.CrossEntropyLoss()

    for epoch in range(args.max_epochs):
        state_h, state_c = model.init_state(args.sequence_length)

        for batch, (x, y) in enumerate(dataloader):
            optimizer.zero_grad()

            y_pred, (state_h, state_c) = model(x, (state_h, state_c))
            loss = criterion(y_pred.transpose(1, 2), y)

            state_h = state_h.detach()
            state_c = state_c.detach()

            loss.backward()
            optimizer.step()

            print({ 'epoch': epoch, 'batch': batch, 'loss': loss.item() })
            if (batch % 50 == 0) and saveStages:
                torch.save({
                    'epoch': epoch,
                    'model_state_dict': model.state_dict(),
                    'optimizer_state_dict': optimizer.state_dict(),
                    'loss': loss.item(),
                    }, "model.pt")
        if saveStages:
            torch.save({
                'epoch': epoch,
                'model_state_dict': model.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'loss': loss.item(),
                }, "model.pt")

def predict(dataset, model, text, next_words=1000):
    model.eval()

    words = text.split(' ')
    state_h, state_c = model.init_state(len(words))

    i = 0
    while True:
        x = torch.tensor([[dataset.word_to_index[w] for w in words[i:]]])
        y_pred, (state_h, state_c) = model(x, (state_h, state_c))

        last_word_logits = y_pred[0][-1]
        p = torch.nn.functional.softmax(last_word_logits / temperature, dim=0).detach().cpu().numpy()
        word_index = np.random.choice(len(last_word_logits), p=p)
        words.append(dataset.index_to_word[word_index])
        
        if i >= next_words:
            if dataset.index_to_word[word_index] == "****4":
                break
        
        i = i+1

    return words

parser = argparse.ArgumentParser()
parser.add_argument('--max-epochs', type=int, default=1)
parser.add_argument('--batch-size', type=int, default=1024)
parser.add_argument('--sequence-length', type=int, default=25)
args = parser.parse_args()

dataset = Dataset(args)

model = Model(dataset)
optimizer = optim.Adam(model.parameters(), lr=0.0005)

#checkpoint = torch.load("modelremote.pt", map_location=torch.device('cpu'))
#checkpoint = torch.load("model.pt")
#model.load_state_dict(checkpoint['model_state_dict'])
#optimizer.load_state_dict(checkpoint['optimizer_state_dict'])

print(model)
from prettytable import PrettyTable
def count_parameters(model):
    table = PrettyTable(["Modules", "Parameters"])
    total_params = 0
    for name, parameter in model.named_parameters():
        if not parameter.requires_grad: continue
        params = parameter.numel()
        table.add_row([name, params])
        total_params+=params
    print(table)
    print(f"Total Trainable Params: {total_params}")
    return total_params
count_parameters(model)

training = True
saveStages = True
temperature = 1

if training:
    train(dataset, model, args)

    torch.save({
        'model_state_dict': model.state_dict(),
        'optimizer_state_dict': optimizer.state_dict(),
        }, "model.pt"+(datetime.now().strftime("%H-%M-%S")))

