# MTG Card Generator
Generate AI Magic the Gathering cards from nothing, or from a subset of card information.  
mtg-card-gen targets nvidia GPUs on Linux, and M series macs.
## Setup
Clone the repo and linked mtg-renderer repo for card rendering.  
Make sure that Gson and mtg-renderer are on the build path.  
Download the latest version of the **Oracle Cards** data from Scryfall at https://scryfall.com/docs/api/bulk-data and place the file in the project directory.  
Run the java OracleExtractor class to generate the machine learning datasets.  
Follow the steps in the Python README to train the models.  
For art generation, set up https://github.com/AUTOMATIC1111/stable-diffusion-webui  
Run stable-diffusion-webui with `./webui.sh --listen --port 7860 --api` to enable the included API on port 7860.  
With stable diffusion running, the card generation pipeline can be run from BulkCardPipeline with arguments  
`<path to ../Python/env/bin/python3> <base python folder location> <card author name> <URL to stable diffusion API...>`  
ex.
`/Users/username/Documents/mtg-card-gen/Python/env/bin/python3 /Users/username/Documents/mtg-card-gen/Python GreenScripter http://localhost:7860 http://192.168.1.200:7860`  
to run with the name `GreenScripter` and two instances of stable diffusion api at `http://localhost:7860` and `http://192.168.1.200:7860`  
The pipeline can fully utilize a large number of stable diffusion instances, as the card generator is much faster than stable diffusion when run on similar hardware.  
Custom card generation code can be written using the Models in the `greenscripter.mtgcardgen.models` java package based on the pipeline. For a fully featured card generation setup on one machine, 8 GB of VRAM is needed to load all models and stable diffusion at once.  
