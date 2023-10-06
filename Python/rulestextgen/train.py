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

parser = argparse.ArgumentParser()
parser.add_argument('--max-epochs', type=int, default=1)
parser.add_argument('--batch-size', type=int, default=512)
parser.add_argument('--sequence-length', type=int, default=100)
args = parser.parse_args()

dataset = Dataset(args)

model = Model(dataset)
optimizer = optim.Adam(model.parameters(), lr=0.0005)

#resume
#checkpoint = torch.load("modelremote.pt", map_location=torch.device('cpu'))
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
