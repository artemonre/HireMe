package com.artemonre.hireme.portfolio

import kotlinx.datetime.LocalDate

data class PortfolioLink(
    val label: String,
    val url: String,
)

data class PortfolioContact(
    val label: String,
    val value: String,
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
    val tagline: String,
    val skills: List<Skill>,
    val experience: List<Experience>,
    val projects: List<Project>,
    val links: List<PortfolioLink>,
    val contacts: List<PortfolioContact>,
    val applicationTechnologies: List<String>,
    val serverTechnologies: List<String>,
)

// All portfolio content lives here — edit this to update what the app displays.
val myProfile = PortfolioProfile(
    name = "Artem Balzan",
    title = "Senior Android developer",
    bio = """
        Passionate about learning and adopting new technologies, which is where I perform best — on new builds or projects undergoing change and migration. I bring broad experience both building products from scratch and modernizing existing ones.
    """.trimIndent(),
    tagline = "Thrives on new builds and modernizing legacy systems — always learning, always adapting.",
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
            highlighted = true,
            description = "Declarative UI toolkit for native Android.",
        ),
        Skill(
            name = "Kotlin Coroutines & Flow",
            description = "Asynchronous and reactive programming; led the migration from RxJava to Coroutines/Flow at Lectera, cutting boilerplate by ~20%.",
        ),
        Skill(
            name = "Clean Architecture",
            highlighted = true,
            description = "Layered, testable architecture approach applied to keep codebases maintainable as they scaled.",
        ),
        Skill(
            name = "Kotlin Multiplatform",
            description = "Sharing business logic and UI across platforms.",
        ),
        Skill(
            name = "Compose Multiplatform",
            description = "Cross-platform declarative UI built on Compose.",
        ),
        Skill(
            name = "Software Architecture",
            description = "Broader architectural decision-making — evaluating trade-offs and setting technical direction, most visible in the Lectera migrations.",
        ),
        Skill(
            name = "Gradle",
            description = "Build system configuration for Android projects, including build variants and dependency management.",
        ),
        Skill(
            name = "AI Pair Programming",
            highlighted = true,
            description = "Using AI coding assistants (e.g. GitHub Copilot, Claude, Cursor) as part of the daily engineering workflow, with the judgment to review, validate, and refine AI-generated code rather than accept it blindly.",
        ),
        Skill(
            name = "Android Jetpack (libraries)",
            description = "Umbrella term for Jetpack components used beyond Compose — ViewModel, Navigation, LiveData, WorkManager-style architecture pieces.",
        ),
        Skill(
            name = "Unit & Integration Testing",
            description = "Writing and maintaining automated tests to protect reliability during refactors and migrations.",
        ),
        Skill(
            name = "MVVM (Model-View-ViewModel)",
            description = "Primary architectural pattern used across Lectera, Preon, and Ru-you projects.",
        ),
        Skill(
            name = "Modular / Multi-module Architecture",
            description = "Splitting large codebases into independent modules for build speed and separation of concerns (Preon).",
        ),
        Skill(
            name = "Legacy System Modernization",
            description = "Planning and executing large migrations on live production code — RxJava → Coroutines/Flow, Media2 → Media3.",
        ),
        Skill(
            name = "Code Refactoring",
            description = "Improving existing code structure and readability without changing behavior, done continuously alongside the Lectera migrations.",
        ),
        Skill(
            name = "Dependency Injection",
            description = "Used both major DI frameworks in the Android ecosystem — Dagger2 at Lectera, Koin at Preon.",
        ),
        Skill(
            name = "REST API Integration",
            description = "Consuming and integrating backend APIs, typically via Retrofit, across every role.",
        ),
        Skill(
            name = "Room",
            description = "Android's SQL persistence library used for local data storage.",
        ),
        Skill(
            name = "SQLite",
            description = "Underlying local database technology behind Room-based storage.",
        ),
        Skill(
            name = "Media Playback (ExoPlayer / Media3)",
            description = "Video/audio playback engineering — architected the Media2 → Media3 migration at Lectera.",
        ),
        Skill(
            name = "Firebase Platform",
            description = "Crash reporting and push notification infrastructure; core to raising Lectera's crash-free rate to 98–99%.",
        ),
        Skill(
            name = "Authentication & Social Sign-In",
            description = "Implementing third-party login flows.",
        ),
        Skill(
            name = "Google Mobile Services (GMS) Integration",
            description = "Integrating standard Google Play Services (location, notifications, etc.) into apps.",
        ),
        Skill(
            name = "Huawei Mobile Services (HMS) Integration",
            description = "Enabling compatibility for non-GMS Huawei devices — a distinctive, less common skill worth calling out explicitly on a CV since few Android developers have it.",
        ),
        Skill(
            name = "In-App Billing & Payments",
            description = "Implementing subscriptions and in-app purchase flows.",
        ),
        Skill(
            name = "Google Play Release Management",
            description = "End-to-end ownership of app releases — build configuration, submission, and Play Console management.",
        ),
        Skill(
            name = "Agile / Scrum",
            description = "Working within sprint-based, ceremony-driven development processes.",
        ),
        Skill(
            name = "Git & Version Control",
            description = "Standard version control workflows across all roles.",
        ),
    ),
    experience = listOf(
        Experience(
            title = "Senior Android Developer",
            employer = "Lectera",
            start = LocalDate(2022, 12, 1),
            end = LocalDate(2026, 4, 30),
            description = """
                EdTech application with different kinds of courses to learn. 5 languages, more than 30 countries, around 10k Android users. Agile, 2 weeks sprints, cross-platform team of: product owner, product manager, Android, IOS, web, backend, analyst, designers, QA.
                I was maintaining the application, making new features, analyzed app problems (from crash analytics and user reviews) and fixed them.
                Important project changes I've led:
                - Migration of the core application from RxJava to Kotlin Coroutines and Flow, simplifying asynchronous code and reducing boilerplate by approximately 20%
                - Architected and executed the transition from Media2 to Media3, improving video playback stability and enabling future feature development
                - Increased application stability to 98–99% crash-free sessions by identifying critical issues and improving error handling
                Kotlin, Coroutines & Flows, MVVM, Clean Architecture, Dagger2, Media3 (ExoPlayer)
            """.trimIndent(),
            url = "https://lectera.com/en",
        ),
        Experience(
            title = "Middle Android developer",
            employer = "Preontech",
            start = LocalDate(2021, 2, 1),
            end = LocalDate(2022, 9, 1),
            description = """
                I was outstaffed to X5 Retail Group, a major retailer, as part of this engagement. I joined their team with Middle+ level, where I owned a few feature-modules of the project for more than 7M Android users.
                My main achievements:
                - Initiated and developed a custom push notification handling system, shifting from default OS behavior to fully app-controlled processing
                - Contributed to the development of a large-scale retail Android application for X5 Retail Group with 7M+ users, working with more than 10 cross-platform teams with more than 13 Android developers
                Kotlin, Coroutines & Flows, MVVM, Clean Architecture, Firebase Cloud Messaging, Firebase Analytics, GitLab (VCS and reviews)
            """.trimIndent(),
            url = "https://preon.tech/",
        ),
        Experience(
            title = "Android developer",
            employer = "Ru-you",
            start = LocalDate(2018, 7, 9),
            end = LocalDate(2021, 2, 1),
            description = """
                It was my first job as an Android developer. It's an outsource company making soft for small-middle business. We had a new project every 3-6 months, so it was a lot of context switching. I was the only person with Android expertise in the company. So, I had to make project from scratch until publication on my own, learning whole process on the go.
                - Quickly progressed to delivering production Android applications and supporting full release cycles
                - Built and published my first production level project in 3 months after joining the company
                - Developed and shipped more than 10 Android applications as a sole Android developer, owning the full development cycle from requirements to Google Play release
                - Was changing and learning new technologies for every new project
                Java/Kotlin, AsynTasks/Coroutines & Flows, MVP/MVVM, Google Billing, ExoPlayer
            """.trimIndent(),
            url = "https://ruyou.ru/",
        ),
    ),
    projects = listOf(
        Project(
            name = "Preontech / Coffee Glitch app",
            duration = "1.5 year",
            description = """
                It was an application for a small coffee-shop chain business.
                I was responsible for the development of the application from scratch. We had a design and some technical requirements. I chose stack and architecture.
                Because it was a project for a very modern coffee-shop I decided it's a good idea to use Jetpack Compose for the UI.
                It was very good and interesting project.
                - because I could experiment and learn new technologies. And it's one of my favorite things in programming
                - because it was a very strong team. I worked with some of them before. And I will be glad to work with them again
                - and because it was a fast progressing project, we moved in a quick pace with some decisions made here and now, brief discussions and good understanding.
            """.trimIndent(),
        ),
        Project(
            name = "Lectera / RXJava -- Coroutines migration",
            duration = "3 months",
            description = """
                The app had been built entirely on RxJava for years.
                When our feature workload eased up, I saw the opportunity to migrate it to Kotlin Coroutines and Flow — a fully Kotlin-native approach that's cleaner to work with and, in my view, where the ecosystem is heading; I believe it will hold up better long-term than RxJava.
                Since RxJava was woven through nearly every layer of the app, this wasn't a small undertaking, so I put together a case for the business explaining the benefits and asked for dedicated time to do it properly rather than squeezing it in alongside feature work.
                The migration turned out to be more complex than expected precisely because of how deeply RxJava was embedded, but it paid off in a codebase that's easier to reason about and maintain.
            """.trimIndent(),
        ),
    ),
    links = listOf(
        PortfolioLink("GitHub", "https://github.com/artemonre"),
        PortfolioLink("LinkedIn", "https://linkedin.com/in/artemonre"),
    ),
    contacts = listOf(
        PortfolioContact("Email", "balzanartem@gmail.com", "mailto:balzanartem@gmail.com"),
        PortfolioContact("Phone (WhatsApp)", "+381629458621", "https://wa.me/381629458621"),
        PortfolioContact("Telegram (nickname)", "@artemonre", "https://t.me/artemonre"),
    ),
    applicationTechnologies = listOf("Kotlin", "Jetpack Compose", "Compose Multiplatform", "Navigation3", "Koin"),
    serverTechnologies = listOf("Firebase", "GitHub Actions"),
)
