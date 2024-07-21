import torch
from transformers import AutoTokenizer, AutoModelForCausalLM

class TextProcessor:
    def __init__(self):
        model_name = "phi3_mini"  # Adjust path as necessary
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModelForCausalLM.from_pretrained(model_name, from_safetensors=True)

    def make_text_more_detailed(self, text):
        inputs = self.tokenizer.encode("make the text be more detailed: " + text, return_tensors="pt")
        outputs = self.model.generate(inputs, max_length=500, num_return_sequences=1)
        return self.tokenizer.decode(outputs[0], skip_special_tokens=True)

text_processor = TextProcessor()

def process_text(text):
    return text_processor.make_text_more_detailed(text)
