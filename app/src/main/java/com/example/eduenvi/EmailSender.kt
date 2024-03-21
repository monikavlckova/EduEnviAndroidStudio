package com.example.eduenvi

import android.util.Log
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailSender {

    private fun sendEmail(receiverEmail: String?, message: String, subject: String) {
        try {
            // Defining sender's email and password
            val senderEmail = "eduenvifmfi@gmail.com"
            val password = "pkhcikrheeespoyu"

            // Seting up email properties
            val properties = System.getProperties()//TODO probem s pripojenim, preco?
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = "587"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"

            // Creating a session with authentication
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, password)
                }
            })

            // Creating a MimeMessage
            val mimeMessage = MimeMessage(session)

            // Adding the recipient's email address
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))

            // Seting the subject and message content
            // You can Specify yours
            mimeMessage.subject = subject
            mimeMessage.setText(message)

            // Creating a separate thread to send the email
            val t = Thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    // Handling messaging exception
                    //Toast.makeText(this@Test, "Error Occured", Toast.LENGTH_SHORT).show()
                    Log.v("DB", "Error Occured ${e.message}")
                    e.printStackTrace()
                }
            }
            t.start()
        } catch (e: AddressException) {
            // Handling address exception
            //Toast.makeText(this@Test, "Error Occured $e", Toast.LENGTH_SHORT).show()
            Log.v("DB", "Error Occured ${e.message}")
        } catch (e: MessagingException) {
            // Handling messaging exception (e.g. network error)
            //Toast.makeText(this@Test, "Error Occured $e", Toast.LENGTH_SHORT).show()
            Log.v("DB", "Error Occured ${e.message}")
        }

        // Displaying a toast message indicating that the email was sent successfully
        //Toast.makeText(this@Test, "Sent Succesfully ", Toast.LENGTH_SHORT).show()
        Log.v("DB", "Sent saccesfully")
    }

    fun sendPassword(receiverEmail: String, login: String, newPassword: String) {
        val subject = "Zabudnuté heslo"
        val text = """
             Dobrý deň $login,
             toto je vaše nové heslo: $newPassword
             Po prihlásení si ho zmeňte!
             """.trimIndent() //TODO zmen
        sendEmail(receiverEmail, text, subject)
    }

    fun sendWelcome(receiverEmail: String, login: String) {
        val subject = "Vitajte v EduEnvi"
        val text = "Dobrý deň $login,\nVitajte v EduEnvi!" //TODO zmen
        sendEmail(receiverEmail, text, subject)
    }
}