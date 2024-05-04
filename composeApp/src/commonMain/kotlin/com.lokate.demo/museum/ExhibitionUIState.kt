package com.lokate.demo.museum

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

data class ExhibitionUIState(
    val title: String,
    val description: AnnotatedString,
    val imagePath: String?,
    val audioUrl: String?,
)

val monaLisa = ExhibitionUIState(
    title = "Mona Lisa",
    description = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 18.sp)) {
            append("Welcome to one of the world's most famous and enigmatic masterpieces – the ")
        }
        pushStringAnnotation(tag = "highlight", annotation = "Mona Lisa")
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Mona Lisa")
        }
        pop()
        append(", painted by the Renaissance genius Leonardo da Vinci between 1503 and 1519. As you stand before this iconic work, let's unravel the mysteries and marvels that have captivated viewers for centuries.\n\n")
        append("Gaze upon the sitter's captivating smile, the subtle play of light and shadow on her face, and the delicate sfumato technique that blurs the boundaries between figure and background. This masterful execution of Leonardo's pioneering techniques has made the Mona Lisa an enduring symbol of Renaissance art and a timeless embodiment of beauty and mystery.\n\n")
        append("But who was this woman, and why did Leonardo choose to immortalize her? Scholars have long debated the identity of the sitter, with theories ranging from Lisa Gherardini, the wife of a Florentine silk merchant, to an idealized representation of Renaissance femininity. Whatever her true identity, the Mona Lisa's enigmatic smile has sparked endless interpretations and debates, inviting us to ponder the depths of human emotion and expression.\n\n")
        append("As you study the painting's intricate details, consider the extraordinary skill and patience required to create such a masterpiece. Leonardo is said to have worked on the Mona Lisa for years, constantly refining and perfecting his brushstrokes, layering glazes and pigments to achieve the remarkable luminosity and lifelike quality that has mesmerized viewers for over five centuries.\n\n")
        append("Beyond its artistic brilliance, the Mona Lisa's journey to the Louvre is a captivating tale in itself. Acquired by King Francis I of France in the early 16th century, the painting survived the upheavals of the French Revolution and even a daring theft in 1911, which only heightened its global fame and allure.\n\n")
        append("So, take a moment to appreciate the Mona Lisa's enduring legacy and the countless interpretations it has inspired in art, literature, and popular culture. Let her enigmatic smile ignite your imagination and invite you to ponder the depths of human creativity and the enduring power of art to captivate and inspire across generations.")
    },
    imagePath = "files/museum/mona_lisa.jpeg",
    audioUrl = "files/museum/mona_lisa.mp3",
)

val venusDeMilo = ExhibitionUIState(
    title = "Venus de Milo",
    description = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 18.sp)) {
            append("As you gaze upon this classical Greek masterpiece, the ")
        }
        pushStringAnnotation(tag = "highlight", annotation = "Venus de Milo")
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append("Venus de Milo")
        }
        pop()
        append(", you are transported back over 2,000 years to the golden age of ancient Greek art and culture. Discovered in 1820 on the Aegean island of Milos, this magnificent sculpture has captivated viewers with its graceful form and timeless beauty.\n\n")
        append("Carved from Parian marble around 130-100 BC, the Venus de Milo is believed to depict the Greek goddess of love and beauty, Aphrodite. Yet her missing arms, whose original positions remain a mystery, have inspired endless debates and theories over the centuries. Was she portraying modesty, or did her arms once hold attributes like a mirror or an apple?\n\n")
        append("Take a moment to admire the exquisite craftsmanship that brings this figure to life. The sculptor's mastery is evident in the naturalistic rendering of the body, the elegant drapery clinging to her form, and the contrapposto stance that suggests motion and vitality. Every curve and contour is a celebration of ideal beauty according to the ancient Greek aesthetic ideals.\n\n")
        append("But the Venus de Milo is more than just a representation of physical perfection. She embodies the reverence the ancient Greeks held for the divine feminine, their appreciation for harmony and proportion, and their belief in the power of beauty to uplift the human spirit.\n\n")
        append("As your gaze travels along her serene face and across her body, consider the countless artists, writers, and thinkers who have found inspiration in her timeless form over the centuries. From the Renaissance masters to modern sculptors, the Venus de Milo has endured as a beacon of artistic expression and a testament to the enduring human quest for beauty and meaning.\n\n")
        append("So, take a moment to appreciate this masterpiece not just as a work of art, but as a window into the rich cultural legacy of ancient Greece and the profound impact it has had on our understanding of beauty, mythology, and the human experience.")
    },
    imagePath = "files/museum/venus_de_milo.jpg",
    audioUrl = "files/museum/venus_de_milo.mp3",
)