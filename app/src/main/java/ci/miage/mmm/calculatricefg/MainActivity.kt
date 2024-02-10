package ci.miage.mmm.calculatricefg

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ci.miage.mmm.calculatricefg.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    // Propriétés de la class
    private lateinit var binding: ActivityMainBinding
    var lastNumeric = false                      // Indique si le dernier bouton cliqué était un chiffre
    var stateError = false                      // Indique si l'application est dans un état d'erreur
    var lastDot = false                         // Indique si le dernier bouton cliqué était le point décimal
    private lateinit var expression: Expression // Variable pour stocker l'expression à évaluer
    private var currentText: String = ""        // Variable pour stocker le texte actuel
    var resultDisplayed = false                 // Nouvelle variable pour suivre l'état du résultat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialisation de la liaison avec la mise en page
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Fonction pour effacer complètement l'affichage et réinitialiser les indicateurs

    fun onAllClearClick(view: View) {
        binding.screen.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.screen.visibility = View.VISIBLE
    }

    // Fonction pour effacer le dernier caractère de l'affichage
    fun onClearClick(view: View) {
        binding.screen.text = ""
        lastNumeric = false
    }

    // Fonction pour supprimer le dernier caractère de l'affichage et évaluer l'expression si c'était un chiffre
    fun onBackClick(view: View) {
        try {
            val currentText = binding.screen.text.toString()

            if (currentText.isNotEmpty()) {
                binding.screen.text = currentText.dropLast(1)

                // Vérifier si le dernier caractère était un chiffre et déclencher l'égal si nécessaire
                if (currentText.last().isDigit()) {
                    onEqual()
                }
            }
        } catch (e: Exception) {
            // Gérer les erreurs ici
            binding.screen.text = ""
            binding.screen.visibility = View.VISIBLE
            Log.e("onBackClick error", e.toString())
        }
    }

    // Fonction pour gérer les clics sur les opérateurs
    fun onOperatorClick(view: View) {
        val operator = (view as Button).text.toString()
        handleOperator(operator)
        // Vérifier si l'application n'est pas dans un état d'erreur et si le dernier bouton cliqué était un chiffre
        if (!stateError && lastNumeric){
            binding.screen.append((view as Button).text)
            lastDot = false
            lastNumeric = false
        }
    }
    fun onMinusOperatorClick(view: View) {
        // Vérifier si l'application n'est pas dans un état d'erreur et si le dernier bouton cliqué était un chiffre
        if (!stateError && lastNumeric){
            binding.screen.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            val operator = (view as Button).text.toString()
            handleOperator(operator)
        }

    }
    fun onPlusOperatorClick(view: View) {
        // Vérifier si l'application n'est pas dans un état d'erreur et si le dernier bouton cliqué était un chiffre
        if (!stateError && lastNumeric) {
            val buttonText = (view as Button).text.toString()
            binding.screen.append(buttonText)
            lastDot = false
            lastNumeric = false
            handleOperator(buttonText)

        }


    }
    // Fonction pour gérer les clics sur les chiffres
    fun onDigitClick(view: View) {
        if (stateError) {
            binding.screen.text = (view as Button).text
            stateError = false
        } else {
            val buttonText = (view as Button).text.toString()

            // Gérer l'opérateur +/- (plus ou moins)
            if (buttonText == "+/-") {
                toggleNegation()
            } else {
                // Vérifier si le résultat est affiché
                if (resultDisplayed) {
                    // Commencer une nouvelle expression avec le chiffre cliqué
                    binding.screen.text = buttonText
                    resultDisplayed = false
                } else {
                    binding.screen.append(buttonText)
                }
            }
        }
        lastNumeric = true
    }



    // Fonction pour gérer les clics sur le bouton +/- (Plus/Minus)

    fun onPlusMinusClick(view: View) {
        toggleNegation()
    }

    // Fonction pour basculer entre la négation et la positivité
    private fun toggleNegation() {
        try {
            // Récupérer le texte actuel
            currentText = binding.screen.text.toString()
            Log.d("CalcDebug", "Current Text: $currentText")

            // Vérifier si le texte commence par un signe moins
            if (currentText.startsWith("-")) {
                // Supprimer le signe moins
                binding.screen.text = currentText.substring(1)
                Log.d("CalcDebug", "Minus removed")
            } else {
                // Ajouter le signe moins
                binding.screen.text = "-$currentText"
                Log.d("CalcDebug", "Minus added")
            }
        } catch (e: Exception) {
            Log.e("CalcError", "Error in toggleNegation", e)
            // En cas d'erreur, ajouter le signe moins
            binding.screen.text = "-$currentText"
        }
    }

    // Fonction pour gérer les clics sur le bouton égal
    fun onEqualClick(view: View) {

        onEqual()
    }

    // Fonction pour évaluer l'expression actuelle
    fun onEqual() {
        // Vérifier si le dernier bouton cliqué était un chiffre et l'application n'est pas dans un état d'erreur
        if (lastNumeric && !stateError) {
            val txt = binding.screen.text.toString()
            expression = ExpressionBuilder(txt).build()

            // Évaluer l'expression et afficher le résultat
            try {
                val result = expression.evaluate()

                // Vérifier si le résultat est un nombre entier
                if (result.toInt().toDouble() == result) {
                    binding.screen.text = result.toInt().toString()
                } else {
                    binding.screen.text = result.toString()
                }

                binding.screen.visibility = View.VISIBLE

            } catch (ex: ArithmeticException) {
                // En cas d'erreur d'évaluation, afficher "Error" et mettre à jour les indicateurs
                Log.e("evaluate error", ex.toString())
                binding.screen.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }


    fun onDotClick(view: View) {
        handleDot()
    }

    private fun handleDot() {
        val currentText = binding.screen.text.toString()

        // Vérifier s'il y a déjà un point décimal dans le nombre
        if (!currentText.contains(".")) {
            binding.screen.append(".")
            lastNumeric = true
        }
    }

    private fun handleOperator(operator: String) {
        if (!stateError && lastNumeric) {
            binding.screen.append(operator)
            lastDot = false
            lastNumeric = false
        }
    }


}