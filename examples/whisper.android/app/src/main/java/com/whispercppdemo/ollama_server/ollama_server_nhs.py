from flask import Flask, request, Response
import ollama

app = Flask(__name__)

@app.route('/convert', methods=['POST'])
def convert_to_fhir():
    data = request.json
    raw_text = data.get("text")
    prompt = data.get("prompt", "Convert the following clinical conversation into a FHIR-enabled record with SNOMED CT codes. FHIR records need to include patient basic information etc. And provide only the FHIR records in JSON format without the prefix statement. Include necessary information. Also at the bottom of the FHIR records, include the link of the Prescribe for that disease in the website of BNF. Here are some examples of the format of the URL: https://bnf.nice.org.uk/treatment-summaries/anaemia-iron-deficiency/. https://bnf.nice.org.uk/treatment-summaries/ear/. https://bnf.nice.org.uk/treatment-summaries/asthma-acute/. Hyphens are used to link each word. You must adjust the URL according to the actual patient disease. So at the bottom, it should be like this: \"Prescribe: https://bnf.nice.org.uk/treatment-summaries/{actual patient disease name in this case}/\". Only show me the FHIR JSON records without explanation, notes, etc.")

    def generate_fhir_record():
        stream = ollama.chat(
            model='phi3',
            messages=[{'role': 'user', 'content': f'{prompt}: {raw_text}'}],
            stream=True,
        )

        for chunk in stream:
            yield chunk['message']['content']



    return Response(generate_fhir_record(), content_type='text/plain')

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
