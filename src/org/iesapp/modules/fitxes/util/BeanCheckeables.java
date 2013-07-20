/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.util;

import java.util.HashMap;

/**
 *
 * @author Josep
 */
class BeanCheckeables {
    protected int id;
    protected String actuacio;
    protected String simbol;
    protected int threshold;
    protected int vrepeticio;
    protected int vmax;
    protected boolean repetir;
    protected String ambits;
    protected String estudis;
    protected int minAge;
    protected int maxAge;
    protected String equivalences;
    protected HashMap<String,String> extensions;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the actuacio
     */
    public String getActuacio() {
        return actuacio;
    }

    /**
     * @param actuacio the actuacio to set
     */
    public void setActuacio(String actuacio) {
        this.actuacio = actuacio;
    }

    /**
     * @return the simbol
     */
    public String getSimbol() {
        return simbol;
    }

    /**
     * @param simbol the simbol to set
     */
    public void setSimbol(String simbol) {
        this.simbol = simbol;
    }

    /**
     * @return the threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * @return the repetir
     */
    public boolean isRepetir() {
        return repetir;
    }

    /**
     * @param repetir the repetir to set
     */
    public void setRepetir(boolean repetir) {
        this.repetir = repetir;
    }

    /**
     * @return the ambits
     */
    public String getAmbits() {
        return ambits;
    }

    /**
     * @param ambits the ambits to set
     */
    public void setAmbits(String ambits) {
        this.ambits = ambits;
    }

    /**
     * @return the estudis
     */
    public String getEstudis() {
        return estudis;
    }

    /**
     * @param estudis the estudis to set
     */
    public void setEstudis(String estudis) {
        this.estudis = estudis;
    }

    /**
     * @return the minAge
     */
    public int getMinAge() {
        return minAge;
    }

    /**
     * @param minAge the minAge to set
     */
    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    /**
     * @return the maxAge
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * @return the equivalences
     */
    public String getEquivalences() {
        return equivalences;
    }

    /**
     * @param equivalences the equivalences to set
     */
    public void setEquivalences(String equivalences) {
        this.equivalences = equivalences;
    }

    public int getVrepeticio() {
        return vrepeticio;
    }

    public void setVrepeticio(int vrepeticio) {
        this.vrepeticio = vrepeticio;
    }

    public int getVmax() {
        return vmax;
    }

    public void setVmax(int vmax) {
        this.vmax = vmax;
    }

    public HashMap<String,String> getExtensions() {
        return extensions;
    }

    public void setExtensions(HashMap<String,String> extensions) {
        this.extensions = extensions;
    }
}
