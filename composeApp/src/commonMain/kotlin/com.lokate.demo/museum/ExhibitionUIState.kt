package com.lokate.demo.museum

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

data class ExhibitionUIState(
    val title: String,
    val description: AnnotatedString,
    val imagePath: String,
    val audioUrl: String,
)

val pieta =
    ExhibitionUIState(
        title = "La Pietà di Michelangelo",
        description =
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("Welcome to one of the most moving and revered sculptures in art history – the ")
                }
                pushStringAnnotation(tag = "highlight", annotation = "Pieta")
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Pieta")
                }
                pop()
                append(
                    ", created by the Renaissance master Michelangelo between 1498 and 1499. As you stand before this breathtaking work, let's explore the profound emotion and exquisite craftsmanship that have made the Pieta a timeless masterpiece.\n\n",
                )
                append(
                    "Marvel at the tender depiction of the Virgin Mary cradling the lifeless body of her son, Jesus Christ, after his crucifixion. Michelangelo's mastery of marble brings to life the raw anguish and sorrow of this moment, capturing the delicate balance between maternal love and divine sacrifice.\n\n",
                )
                append(
                    "The Pieta is renowned for its technical virtuosity and emotional depth. Notice the intricate details of Mary's robes, the softness of her expression, and the serene beauty of Christ's face, despite the wounds of his ordeal. Michelangelo's ability to imbue stone with such lifelike warmth and emotion is a testament to his unparalleled skill and sensitivity as a sculptor.\n\n",
                )
                append(
                    "But the Pieta is more than just a masterpiece of artistry – it is a profound meditation on grief, compassion, and the redemptive power of love. As you contemplate the sculpture's haunting beauty, consider the universal themes of suffering and salvation that resonate across time and culture.\n\n",
                )
                append(
                    "Beyond its artistic significance, the Pieta holds a special place in Christian iconography and devotion. Originally commissioned for the tomb of a French cardinal in St. Peter's Basilica, the sculpture has inspired countless pilgrims and worshippers with its profound spiritual resonance.\n\n",
                )
                append(
                    "So, take a moment to reflect on the timeless beauty and spiritual significance of the Pieta. Let Michelangelo's masterpiece inspire you to contemplate the mysteries of faith, love, and redemption, and to find solace in the enduring power of art to touch our hearts and souls.",
                )
            },
        imagePath = "files/museum/pieta.jpg",
        audioUrl = "files/museum/pieta.mp3",
    )

val schoolOfAthens =
    ExhibitionUIState(
        title = "The School of Athens",
        description =
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("Welcome to one of the most celebrated frescoes of the Renaissance – ")
                }
                pushStringAnnotation(tag = "highlight", annotation = "The School of Athens")
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("The School of Athens")
                }
                pop()
                append(
                    ", painted by the Italian artist Raphael between 1509 and 1511. As you explore this magnificent masterpiece, let's delve into the rich symbolism and intellectual brilliance that have made The School of Athens an enduring icon of Western art.\n\n",
                )
                append(
                    "Step into the grandeur of the Vatican's Apostolic Palace and behold the awe-inspiring vision of Raphael's fresco. Transported to the world of ancient Greece, you'll find yourself amidst a gathering of history's greatest philosophers, mathematicians, and scholars.\n\n",
                )
                append(
                    "At the center of the composition, stand Plato and Aristotle – two towering figures whose contrasting philosophies shaped the course of Western thought. Notice the subtle gestures and expressions that reflect their respective teachings: Plato, pointing upwards to the realm of ideals, and Aristotle, gesturing towards the earth, grounded in empirical observation.\n\n",
                )
                append(
                    "Surrounding them are other luminaries of antiquity, engaged in lively discussions and contemplations. From Pythagoras and Euclid to Socrates and Diogenes, each figure embodies a facet of human intellect and inquiry, symbolizing the pursuit of knowledge and wisdom.\n\n",
                )
                append(
                    "But The School of Athens is more than just a gathering of historical figures – it is a visual feast of Renaissance ideals and aspirations. Notice the architectural harmony and perspective that draw your eye towards the vanishing point, inviting you to explore the depth of space and thought.\n\n",
                )
                append(
                    "As you immerse yourself in the intricate details of Raphael's composition, consider the enduring relevance of the intellectual pursuits depicted here. The School of Athens serves as a timeless reminder of the power of education, dialogue, and critical thinking to illuminate the human experience.\n\n",
                )
                append(
                    "So, take a moment to appreciate the brilliance of Raphael's masterpiece and the timeless wisdom it encapsulates. Let The School of Athens inspire you to embrace the pursuit of knowledge and the exchange of ideas, echoing through the corridors of history and shaping the world we inhabit.",
                )
            },
        imagePath = "files/museum/school_of_athens.jpg",
        audioUrl = "files/museum/school_of_athens.mp3",
    )

val monaLisa =
    ExhibitionUIState(
        title = "Mona Lisa",
        description =
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("Welcome to one of the world's most famous and enigmatic masterpieces – the ")
                }
                pushStringAnnotation(tag = "highlight", annotation = "Mona Lisa")
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Mona Lisa")
                }
                pop()
                append(
                    ", painted by the Renaissance genius Leonardo da Vinci between 1503 and 1519. As you stand before this iconic work, let's unravel the mysteries and marvels that have captivated viewers for centuries.\n\n",
                )
                append(
                    "Gaze upon the sitter's captivating smile, the subtle play of light and shadow on her face, and the delicate sfumato technique that blurs the boundaries between figure and background. This masterful execution of Leonardo's pioneering techniques has made the Mona Lisa an enduring symbol of Renaissance art and a timeless embodiment of beauty and mystery.\n\n",
                )
                append(
                    "But who was this woman, and why did Leonardo choose to immortalize her? Scholars have long debated the identity of the sitter, with theories ranging from Lisa Gherardini, the wife of a Florentine silk merchant, to an idealized representation of Renaissance femininity. Whatever her true identity, the Mona Lisa's enigmatic smile has sparked endless interpretations and debates, inviting us to ponder the depths of human emotion and expression.\n\n",
                )
                append(
                    "As you study the painting's intricate details, consider the extraordinary skill and patience required to create such a masterpiece. Leonardo is said to have worked on the Mona Lisa for years, constantly refining and perfecting his brushstrokes, layering glazes and pigments to achieve the remarkable luminosity and lifelike quality that has mesmerized viewers for over five centuries.\n\n",
                )
                append(
                    "Beyond its artistic brilliance, the Mona Lisa's journey to the Louvre is a captivating tale in itself. Acquired by King Francis I of France in the early 16th century, the painting survived the upheavals of the French Revolution and even a daring theft in 1911, which only heightened its global fame and allure.\n\n",
                )
                append(
                    "So, take a moment to appreciate the Mona Lisa's enduring legacy and the countless interpretations it has inspired in art, literature, and popular culture. Let her enigmatic smile ignite your imagination and invite you to ponder the depths of human creativity and the enduring power of art to captivate and inspire across generations.",
                )
            },
        imagePath = "files/museum/mona_lisa.jpeg",
        audioUrl = "files/museum/mona_lisa.mp3",
    )

val venusDeMilo =
    ExhibitionUIState(
        title = "Venus de Milo",
        description =
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 18.sp)) {
                    append("As you gaze upon this classical Greek masterpiece, the ")
                }
                pushStringAnnotation(tag = "highlight", annotation = "Venus de Milo")
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append("Venus de Milo")
                }
                pop()
                append(
                    ", you are transported back over 2,000 years to the golden age of ancient Greek art and culture. Discovered in 1820 on the Aegean island of Milos, this magnificent sculpture has captivated viewers with its graceful form and timeless beauty.\n\n",
                )
                append(
                    "Carved from Parian marble around 130-100 BC, the Venus de Milo is believed to depict the Greek goddess of love and beauty, Aphrodite. Yet her missing arms, whose original positions remain a mystery, have inspired endless debates and theories over the centuries. Was she portraying modesty, or did her arms once hold attributes like a mirror or an apple?\n\n",
                )
                append(
                    "Take a moment to admire the exquisite craftsmanship that brings this figure to life. The sculptor's mastery is evident in the naturalistic rendering of the body, the elegant drapery clinging to her form, and the contrapposto stance that suggests motion and vitality. Every curve and contour is a celebration of ideal beauty according to the ancient Greek aesthetic ideals.\n\n",
                )
                append(
                    "But the Venus de Milo is more than just a representation of physical perfection. She embodies the reverence the ancient Greeks held for the divine feminine, their appreciation for harmony and proportion, and their belief in the power of beauty to uplift the human spirit.\n\n",
                )
                append(
                    "As your gaze travels along her serene face and across her body, consider the countless artists, writers, and thinkers who have found inspiration in her timeless form over the centuries. From the Renaissance masters to modern sculptors, the Venus de Milo has endured as a beacon of artistic expression and a testament to the enduring human quest for beauty and meaning.\n\n",
                )
                append(
                    "So, take a moment to appreciate this masterpiece not just as a work of art, but as a window into the rich cultural legacy of ancient Greece and the profound impact it has had on our understanding of beauty, mythology, and the human experience.",
                )
            },
        imagePath = "files/museum/venus_de_milo.jpg",
        audioUrl = "files/museum/venus_de_milo.mp3",
    )
