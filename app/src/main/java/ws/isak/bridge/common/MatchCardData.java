package ws.isak.bridge.common;

import ws.isak.bridge.R;


/*
 * Class MatchCardData constructs the object that comprises one tile element in a matchTheme.  This is one
 * or more image files (pairs of images are needed for the birds matchTheme, currently the spectrograms
 * matchTheme will only ask users to match identical spectrograms) and an audio file.
 *
 * @author isak
 */

public class MatchCardData
{
    private final String TAG = "MatchCardData";

    private int cardID;                     //FIXME Private Key? Tile ID between 1 and the number of tiles objects in resources
    private String speciesName;             //this string stores the species name from R.strings.species_bird_% FIXME make dynamic?

    private boolean pairedImagesDiffer;     //boolean true if there is a second image in the pair
    private boolean firstImageUsed;         //switches to true when the object is accessed a second time

    private String imageURI0;               //path to a blank image for the blank tile game
    private String imageURI1;               //path to the first image file if the object requires paired images
    private String imageURI2;               //path to second image file of pair if necessary
    private String imageURI3;               //path to spectrogram image file for spectrogram matchTheme

    private String audioURI;                //path to audio file for object
    private long sampleDuration;

    /*
     * Method setCardIDKey sets the ID value for a card object to @param id
     */
    public void setCardID (int id) {
        try {
            //Log.d (TAG, "method setCardIDKey: id : " + id);
            cardID = id;
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }

    /*
     * Method getCardIDKey returns the @param id associated with a card object
     */
    public int getCardID () {
        try {
            //Log.d(TAG, "method: getCardIDKey: return: " + cardID);
            return cardID;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return -1;
        }
    }

    /*
     * Methods to set/get the speciesName (to be used in a toast when a pair matches)
     */
    public void setSpeciesName (int id) {
        try {
            //Log.d (TAG, "method setSpeciesName: species: " + species);
            switch (id) {
                case 1:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_1);
                    break;
                case 2:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_2);
                    break;
                case 3:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_3);
                    break;
                case 4:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_4);
                    break;
                case 5:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_5);
                    break;
                case 6:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_6);
                    break;
                case 7:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_7);
                    break;
                case 8:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_8);
                    break;
                case 9:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_9);
                    break;
                case 10:
                    speciesName = Shared.context.getResources().getString(R.string.species_bird_10);
                    break;
            }
        }
        catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }

    //FIXME Overloaded method call for retrieval from database
    public void setSpeciesName (String name) {
        speciesName = name;
    }

    public String getSpeciesName () {
        try {
            //Log.d (TAG, "method getSpeciesName: return: species: " + speciesName);
            return speciesName;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    //Image URI 0 holds the blank image for all cards in audio only mode
    public void setImageURI0 (String URI0) {
        //Log.d (TAG, "method setImageURI0: imageURI0 : " + URI0);
        imageURI0 = URI0;

    }

    public String getImageURI0 () {
        try {
            //Log.d (TAG, "method: getImageURI0: return: " + imageURI0);
            return imageURI0;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    //Image URIs 1 & 2 hold paired images for bird image match mode
    public void setImageURI1 (String URI1) {
        //Log.d (TAG, "method setImageURI1: imageURI1 : " + URI1);
        imageURI1 = URI1;

    }

    public String getImageURI1 () {
        try {
            //Log.d (TAG, "method: getImageURI1: return: " + imageURI1);
            return imageURI1;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setImageURI2 (String URI2) {
        //Log.d (TAG, "method: setImageURI2: imageURI2 : " + URI2);
        imageURI2 = URI2;
    }

    public String getImageURI2 () {
        try {
            //Log.d (TAG, "method: getImageURI2: returns:: " + imageURI2);
            return imageURI2;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    //Image URI 3 holds the images for spectrogram mode
    public void setImageURI3 (String URI3) {
        //Log.d (TAG, "method setImageURI3: imageURI1 : " + URI3);
        imageURI3 = URI3;

    }

    public String getImageURI3 () {
        try {
            //Log.d (TAG, "method: getImageURI3: return: " + imageURI3);
            return imageURI3;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setPairedImageDiffer (boolean pairDifferent) {
        //Log.d (TAG, "method: setPairedImagedDiffer: var pairDifferent is : " + pairDifferent);
        pairedImagesDiffer = pairDifferent;
    }

    public boolean getPairedImageDiffer () {
        try {
            //Log.d (TAG, "method: getPairedImageDiffer: returns: " + pairedImagesDiffer);
            return pairedImagesDiffer;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;       //TODO does this make sense?
        }
    }

    public void setFirstImageUsed (boolean usedOnce) {
        //Log.d (TAG, "method: setFirstImageUsed: var usedOnce is: " + usedOnce);
        firstImageUsed = usedOnce;
    }

    public boolean getFirstImageUsed () {
        try {
            //Log.d (TAG, "method: getFirstImageUsed: returns: " + firstImageUsed);
            return firstImageUsed;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return false;           //TODO does this make sense as a return value for an attempt to return from a null pointer?
        }
    }

    public void setAudioURI (String URI) {
        //Log.d (TAG, "method: setAudioURI: audioURI : " + URI);
        audioURI = URI;
    }

    public String getAudioURI () {
        try {
            //Log.d (TAG, "method: getAudioURI: returns: " + audioURI);
            return audioURI;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public void setSampleDuration (long dur) {
        //Log.d (TAG, "method: setSampleDuration: sampleDuration: " + dur);
        sampleDuration = dur;
    }

    public long getSampleDuration () {
        try {
            //Log.d (TAG, "method getSampleDuration: returns: " + sampleDuration);
            return sampleDuration;
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            return -1;
        }
    }
}