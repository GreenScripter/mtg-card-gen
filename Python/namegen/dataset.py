import torch
from collections import Counter

class Dataset(torch.utils.data.Dataset):
    def __init__(
        self,
        args,
    ):
        self.args = args
        self.words = self.load_words()
        self.uniq_words = self.get_uniq_words()

        self.index_to_word = {index: word for index, word in enumerate(self.uniq_words)}
        self.word_to_index = {word: index for index, word in enumerate(self.uniq_words)}

        self.words_indexes = [self.word_to_index[w] for w in self.words]

    def load_words(self):
        with open(('data/cardtoname.txt', 'data/nametocard.txt')[self.args.from_name], 'r') as file:
            data = file.read().replace('\n', ' ')
        return data.split(' ')

    def get_uniq_words(self):
        with open(('data/cardtoname.txt', 'data/nametocard.txt')[self.args.from_name], 'r') as file:
            data = file.read().replace('\n', ' ')
        word_counts = Counter(data.split(' '))
        return sorted(word_counts, key=word_counts.get, reverse=True)

    def __len__(self):
        return len(self.words_indexes) - self.args.sequence_length

    def __getitem__(self, index):
        return (
            torch.tensor(self.words_indexes[index:index+self.args.sequence_length]),
            torch.tensor(self.words_indexes[index+1:index+self.args.sequence_length+1]),
        )
