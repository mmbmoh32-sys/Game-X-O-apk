package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ARABIC("ar", "Arabic", "العربية"),
    ENGLISH("en", "English", "English")
}

object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إكس أو نيون"
        AppLanguage.ENGLISH -> "NEON XO"
    }

    fun appSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تحدي النيون السيبراني المباشر"
        AppLanguage.ENGLISH -> "REAL-TIME CYBER MULTIPLAYER"
    }

    // Modes
    fun modeOnline(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اللعب مع الأصدقاء"
        AppLanguage.ENGLISH -> "Play with Friends"
    }
    fun modeOnlineDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "غرفة خاصة برمز من 6 خانات ومزامنة فورية"
        AppLanguage.ENGLISH -> "Private 6-character room codes & real-time sync"
    }

    fun modeAi(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اللعب ضد الذكاء الاصطناعي"
        AppLanguage.ENGLISH -> "Play vs Smart AI"
    }
    fun modeAiDesc(lang: AppLanguage, diff: String) = when (lang) {
        AppLanguage.ARABIC -> "مستوى الصعوبة: $diff"
        AppLanguage.ENGLISH -> "Difficulty: $diff"
    }

    fun modeLocal(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "لاعبان على نفس الجهاز"
        AppLanguage.ENGLISH -> "Pass & Play"
    }
    fun modeLocalDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تحدي محلي بين لاعبين على شاشة واحدة"
        AppLanguage.ENGLISH -> "Local 2 players on a single device"
    }

    fun modeStats(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الملف الشخصي والإحصائيات"
        AppLanguage.ENGLISH -> "Stats & Profile"
    }

    // Header & HUD
    fun winRate(lang: AppLanguage, rate: Int) = when (lang) {
        AppLanguage.ARABIC -> "نسبة الفوز: $rate%"
        AppLanguage.ENGLISH -> "Win Rate: $rate%"
    }

    fun yourTurn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "دورك الآن"
        AppLanguage.ENGLISH -> "YOUR TURN"
    }

    fun waitingTurn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "في الانتظار"
        AppLanguage.ENGLISH -> "WAITING"
    }

    fun seconds(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "ثانية"
        AppLanguage.ENGLISH -> "SEC"
    }

    // Lobby
    fun onlineLobby(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "ردهة اللعب عبر الإنترنت"
        AppLanguage.ENGLISH -> "ONLINE LOBBY"
    }

    fun yourRoomCode(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "رمز الغرفة الخاص بك"
        AppLanguage.ENGLISH -> "YOUR ROOM CODE"
    }

    fun copy(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "نسخ"
        AppLanguage.ENGLISH -> "Copy"
    }

    fun shareLink(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مشاركة الرابط"
        AppLanguage.ENGLISH -> "Share Link"
    }

    fun waitingForFriend(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "في انتظار انضمام الصديق..."
        AppLanguage.ENGLISH -> "Waiting for friend to join..."
    }

    fun instantMatchTest(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مباراة فورية (تجربة / منافس)"
        AppLanguage.ENGLISH -> "Instant Match (Test / Rival)"
    }

    fun orJoinRoom(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> " أو انضم إلى غرفة "
        AppLanguage.ENGLISH -> " OR JOIN A ROOM "
    }

    fun enterRoomCode(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أدخل رمز الغرفة"
        AppLanguage.ENGLISH -> "ENTER ROOM CODE"
    }

    fun joinGame(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "انضم للمباراة"
        AppLanguage.ENGLISH -> "Join Game"
    }

    // In-game & Chat
    fun quickChatTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "رسائل سريعة"
        AppLanguage.ENGLISH -> "QUICK CHAT"
    }

    // Game Over
    fun victoryTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "فوز ساحق! 🔥"
        AppLanguage.ENGLISH -> "VICTORY! 🔥"
    }

    fun defeatTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "هزيمة! 💀"
        AppLanguage.ENGLISH -> "DEFEAT! 💀"
    }

    fun drawTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تعادل! ⚖️"
        AppLanguage.ENGLISH -> "DRAW MATCH"
    }

    fun victorySubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "حركات ذكية! لقد سيطرت على الشبكة بالكامل."
        AppLanguage.ENGLISH -> "Dominant moves! You conquered the grid."
    }

    fun defeatSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "حظاً أوفر في الجولة القادمة! راجع خطتك وحاول مجدداً."
        AppLanguage.ENGLISH -> "Better luck next round! Analyze your moves."
    }

    fun drawSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "لعب متكافئ ودفاع قوي من كلا الطرفين!"
        AppLanguage.ENGLISH -> "Perfect play from both sides! No winner."
    }

    fun playAgain(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إعادة المباراة (جولة ثانية)"
        AppLanguage.ENGLISH -> "PLAY AGAIN (REMATCH)"
    }

    fun returnToMenu(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "العودة للقائمة الرئيسية"
        AppLanguage.ENGLISH -> "Return to Menu"
    }

    // Profile & Stats
    fun playerProfileTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الملف الشخصي"
        AppLanguage.ENGLISH -> "PLAYER PROFILE"
    }

    fun displayName(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اسم اللاعب"
        AppLanguage.ENGLISH -> "DISPLAY NAME"
    }

    fun chooseAvatar(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اختر شخصيتك"
        AppLanguage.ENGLISH -> "CHOOSE AVATAR"
    }

    fun careerStats(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الإحصائيات الكلية"
        AppLanguage.ENGLISH -> "CAREER STATS"
    }

    fun winsLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الفوز"
        AppLanguage.ENGLISH -> "WINS"
    }

    fun lossesLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الخسارة"
        AppLanguage.ENGLISH -> "LOSSES"
    }

    fun drawsLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "التعادل"
        AppLanguage.ENGLISH -> "DRAWS"
    }

    fun winRateLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "نسبة الفوز"
        AppLanguage.ENGLISH -> "WIN RATE"
    }

    fun currentStreakLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "السلسلة الحالية"
        AppLanguage.ENGLISH -> "CURRENT STREAK"
    }

    fun bestStreakLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أفضل سلسلة"
        AppLanguage.ENGLISH -> "BEST STREAK"
    }

    fun gamesPlayed(lang: AppLanguage, total: Int) = when (lang) {
        AppLanguage.ARABIC -> "$total مباراة تم لعبها"
        AppLanguage.ENGLISH -> "$total Games Played"
    }

    // Settings
    fun settingsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إعدادات اللعبة"
        AppLanguage.ENGLISH -> "Game Settings"
    }

    fun sfxLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "المؤثرات الصوتية (SFX)"
        AppLanguage.ENGLISH -> "Sound Effects (SFX)"
    }

    fun hapticsLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الاهتزاز التفاعلي"
        AppLanguage.ENGLISH -> "Haptic Vibrations"
    }

    fun languageLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اللغة (Language)"
        AppLanguage.ENGLISH -> "Language (اللغة)"
    }

    fun resetStats(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إعادة ضبط الإحصائيات"
        AppLanguage.ENGLISH -> "Reset Career Stats"
    }

    fun done(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تم"
        AppLanguage.ENGLISH -> "Done"
    }

    fun cancel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إلغاء"
        AppLanguage.ENGLISH -> "Cancel"
    }

    // AI Dialog
    fun selectAiDifficulty(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اختر مستوى الذكاء الاصطناعي"
        AppLanguage.ENGLISH -> "Select AI Difficulty"
    }

    fun getDifficultyLabel(diff: AiDifficulty, lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> when (diff) {
            AiDifficulty.EASY -> "عادي (سهل)"
            AiDifficulty.MEDIUM -> "تكتيكي (متوسط)"
            AiDifficulty.UNBEATABLE -> "خارق لا يُقهر (مينيماكس)"
        }
        AppLanguage.ENGLISH -> diff.label
    }

    fun getDifficultyDesc(diff: AiDifficulty, lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> when (diff) {
            AiDifficulty.EASY -> "خصم خفيف يرتكب أخطاء متكررة"
            AiDifficulty.MEDIUM -> "خصم ذكي يحجب حركات فوزك"
            AiDifficulty.UNBEATABLE -> "ذكاء رياضي مثالي ومدروس، مستحيل هزيمته!"
        }
        AppLanguage.ENGLISH -> diff.description
    }

    fun getRankTitle(wins: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> when {
            wins >= 50 -> "أستاذ النيون الأكبر 👑"
            wins >= 25 -> "بطل السايبر ⚡"
            wins >= 10 -> "محارب الشبكة ⚔️"
            wins >= 3 -> "متحدي النيون 🎮"
            else -> "منافس مبتدئ 🔰"
        }
        AppLanguage.ENGLISH -> when {
            wins >= 50 -> "Neon Grandmaster 👑"
            wins >= 25 -> "Cyber Champion ⚡"
            wins >= 10 -> "Grid Veteran ⚔️"
            wins >= 3 -> "Neon Challenger 🎮"
            else -> "Rookie Contender 🔰"
        }
    }

    fun getQuickChats(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.ARABIC -> listOf(
            "لعبة رائعة! 👏",
            "أسرع! ⏳",
            "حركة ذكية! 🔥",
            "أوبس! 😅",
            "جولة ثانية؟ ⚔️",
            "لعب أسطوري! ⚡",
            "مستحيل! 😱",
            "كش ملك! 👑"
        )
        AppLanguage.ENGLISH -> listOf(
            "Good game! 👏",
            "Hurry up! ⏳",
            "Nice move! 🔥",
            "Oops! 😅",
            "Rematch? ⚔️",
            "GG! You're good! ⚡",
            "No way! 😱",
            "Checkmate! 👑"
        )
    }

    fun getShareText(code: String, lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "⚔️ انضم لمباراتي في لعبة إكس أو نيون (Neon XO)!\nرمز الغرفة: $code\nدعنا نرى من سيسيطر على الشبكة! 🔥"
        AppLanguage.ENGLISH -> "⚔️ Join my Neon XO Tic-Tac-Toe match!\nRoom Code: $code\nLet's see who dominates the grid! 🔥"
    }
}
