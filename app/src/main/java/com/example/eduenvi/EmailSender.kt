package com.example.eduenvi

import android.content.Context
import android.widget.Toast
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

        private fun sendEmail(receiverEmail: String?, message: String, subject: String, context: Context) {
        try {
            val senderEmail = "eduenvifmfi@gmail.com"
            val password = "unjogqgqkrwyyena"

            val properties = System.getProperties()
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, password)
                }
            })

            val mimeMessage = MimeMessage(session)

            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))

            mimeMessage.subject = subject
            mimeMessage.setText(message)

            val t = Thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    // Handling messaging exception
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            t.start()
        } catch (e: AddressException) {
            // Handling address exception
            Toast.makeText(context, "Error Occured $e", Toast.LENGTH_SHORT).show()
        } catch (e: MessagingException) {
            // Handling messaging exception (e.g. network error)
            Toast.makeText(context, "Error Occured $e", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(context, "Sent Succesfully ", Toast.LENGTH_SHORT).show()
    }

    fun sendPassword(receiverEmail: String, login: String, newPassword: String, context: Context) {
        val subject = "Zabudnuté heslo"
        val text = """
             Dobrý deň $login,
             toto je vaše nové heslo: $newPassword
             Po prihlásení si ho zmeňte!
             """.trimIndent()
        sendEmail(receiverEmail, text, subject, context)
    }

    fun sendWelcome(receiverEmail: String, login: String, context: Context) {
        val subject = "Vitajte v EduEnvi"
        val text = "Dobrý deň $login,\nVitajte v EduEnvi!"
        sendEmail(receiverEmail, text, subject, context)
    }
}