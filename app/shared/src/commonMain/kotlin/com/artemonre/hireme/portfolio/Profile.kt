package com.artemonre.hireme.portfolio

import kotlinx.datetime.LocalDate

data class PortfolioLink(
    val label: String,
    val url: String,
)

data class Skill(
    val name: String,
    val highlighted: Boolean = false,
    val description: String = "",
)

data class Experience(
    val title: String,
    val employer: String,
    val start: LocalDate,
    val end: LocalDate? = null,
    val description: String,
    val url: String? = null,
)

data class Project(
    val name: String,
    val duration: String,
    val description: String,
)

data class PortfolioProfile(
    val name: String,
    val title: String,
    val bio: String,
    val skills: List<Skill>,
    val experience: List<Experience>,
    val projects: List<Project>,
    val links: List<PortfolioLink>,
    val contacts: List<PortfolioLink>,
    val applicationTechnologies: List<String>,
    val serverTechnologies: List<String>,
)

// All portfolio content lives here — edit this to update what the app displays.
val myProfile = PortfolioProfile(
    name = "Artem Balzan",
    title = "Senior Android developer",
    bio = "A short bio about yourself goes here. Replace this placeholder with your own introduction.",
    skills = listOf(
        Skill(
            name = "Android",
            highlighted = true,
            description = "Native Android app development with modern tooling.",
        ),
        Skill(
            name = "Kotlin",
            highlighted = true,
            description = "Primary language for backend, Android, and multiplatform work.",
        ),
        Skill(
            name = "Jetpack Compose",
            description = "Declarative UI toolkit for native Android.",
        ),
        Skill(
            name = "Kotlin Multiplatform",
            description = "Sharing business logic and UI across platforms.",
        ),
        Skill(
            name = "Compose Multiplatform",
            description = "Cross-platform declarative UI built on Compose.",
        ),
    ),
    experience = listOf(
        Experience(
            title = "Your Job Title",
            employer = "Employer Name",
            start = LocalDate(2022, 1, 1),
            end = null,
            description = "A short description of your role and impact goes here.",
            url = "https://example.com",
        ),
        Experience(
            title = "Previous Job Title",
            employer = "Previous Employer",
            start = LocalDate(2019, 6, 1),
            end = LocalDate(2021, 12, 1),
            description = "A short description of your role and impact goes here.",
            url = null,
        ),
    ),
    projects = listOf(
        Project(
            name = "Employer / App Name",
            duration = "6 months",
            description = "A short description of this project goes here.",
        ),
        Project(
            name = "Another Project",
            duration = "2023",
            description = "A short description of this project goes here.",
        ),
    ),
    links = listOf(
        PortfolioLink("GitHub", "https://github.com/artemonre"),
        PortfolioLink("LinkedIn", "https://linkedin.com/in/artemonre"),
    ),
    contacts = listOf(
        PortfolioLink("Email", "mailto:balzanartem@gmail.com"),
    ),
    applicationTechnologies = listOf("Kotlin", "Jetpack Compose", "Compose Multiplatform"),
    serverTechnologies = listOf("Firebase", "GitHub Actions"),
)
