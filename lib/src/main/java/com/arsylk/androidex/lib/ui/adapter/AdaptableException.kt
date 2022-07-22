package com.arsylk.androidex.lib.ui.adapter


class AdaptableNotFoundByPosition(position: Int) : IllegalStateException(
    "Adaptable not found for position: $position"
)
class AdaptableNotFoundByViewType(viewType: Int) : IllegalStateException(
    "Adaptable not found for view type: $viewType"
)
class AdaptableViewTypeNotFound(position: Int) : IllegalStateException(
    "Adaptable view type not found for position: $position"
)