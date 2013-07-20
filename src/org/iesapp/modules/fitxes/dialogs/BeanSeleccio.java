/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxes.dialogs;

/**
 *
 * @author Josep
 */
public class BeanSeleccio {

        protected String abrev;
        protected int idProfe;
        protected String materia;
        protected int idAsig;
        protected int idGrupAsig;
        protected String nomProfe;

        public BeanSeleccio()
        {
            
        }
        
        public BeanSeleccio(String nomProfe, String abrev, int idProfe, String materia, int idAsig, int idGrupAsig ) {
            this.nomProfe = nomProfe;
            this.abrev = abrev;
            this.idProfe = idProfe;
            this.materia = materia;
            this.idAsig = idAsig;
            this.idGrupAsig = idGrupAsig;
        }
        
        @Override
        public String toString()
        {
            return "nomProfe:"+this.nomProfe+"; abrev:"+this.abrev+"; idProfe:"+this.idProfe+
                    "; materia:"+this.materia+"; idAsig:"+this.idAsig+"; idGrupAsig:"+this.idGrupAsig;
        }
    }
     