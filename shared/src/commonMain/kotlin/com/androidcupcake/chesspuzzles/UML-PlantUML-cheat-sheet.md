# UML / PlantUML Cheat Sheet

## Visibility modifiers (prefix on members)

| Symbol | Meaning |
|--------|---------|
| `+` | public |
| `-` | private |
| `#` | protected |
| `~` | package-private / internal |

Example: `-piece: Piece` means `piece` is a private field.

## Text style meaning

| Style | Meaning |
|-------|---------|
| *Italic* name | abstract class or method |
| Underlined name | static member |

## Relationship arrows

| Arrow                                                                                                                  | Name | Meaning                                                                                                                                             |
|------------------------------------------------------------------------------------------------------------------------|------|-----------------------------------------------------------------------------------------|
| ![arrow1](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/inheritance.png)                | **Inheritance** (extends) | solid line, hollow triangle — subclass IS-A superclass                                                                                              |
| ![arrow2](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/realization_implementation.png) | **Realization** (implements) | dashed line, hollow triangle — class implements an interface                                                                                        |
| ![arrow3](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/association.png)                | **Association** | solid line, open arrow — one class calls/holds a reference to another                                                                               |
| ![arrow4](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/dependency.png)                 | **Dependency** | dashed line, open arrow — one class uses another temporarily<br>(e.g. a method parameter or return type), without holding a permanent reference     |
| ![arrow5](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/aggregation.png)                | **Aggregation** | solid line, hollow diamond — "has-a" relationship where the<br>parts can exist independently of the whole                                           |
| ![arrow6](file:///home/alord/Documents/Documents/Creative/Assets/UML%20Legend%20Arrows/composition.png)                | **Composition** | solid line, filled diamond — stronger "has-a" where the parts'<br>lifecycle is owned by the whole (if the container is destroyed, so are the parts) |
| ___                                                                                                                    | **Link** | plain solid line — a generic relationship with no specific semantics                                                                                |

### Rule of thumb for picking one
- Class fulfills a contract/interface? → **Realization** (`--|>`)
- "Owns a list of / permanently holds"? → **Composition/Aggregation** (`*--` / `o--`)
- "Temporarily uses / passes through a function"? → **Dependency** (`- - >`)
- "Just calls it"? → **Association** (`-->`)

## Multiplicity (near the connection ends)

| Notation | Meaning |
|----------|---------|
| `1` | exactly one |
| `0..1` | zero or one |
| `0..*` or `*` | zero or many |
| `1..*` | one or many |
| `0..32` | a bounded range (e.g. max 32 chess pieces) |

Written right next to the arrow: `Board o-- "0..32" Piece`

## Stereotypes

`<<...>>` labels add extra meaning to a class/interface that plain UML shapes don't capture — e.g. `<<interface>>`, `<<enum>>`, `<<abstract>>`, or a custom one like `<<Voyager>>` to flag it comes from a specific library.

## Notes

`note right of X` / `note left of X` / `note bottom of X` attach a floating comment box to an element — useful for calling out a design pattern or a non-obvious behavior without cluttering the class body.

## Quick reference applied to the chess diagram

- `Pawn --|> Piece` — Pawn **implements** the Piece interface (realization)
- `Board o-- Piece` — Board **aggregates** pieces (they could theoretically be reused/moved elsewhere)
- `Board - - > BoardSettings` — Board **depends on** settings only when saving (not a permanent reference)
- `Piece - - > PieceMovementBuilder` — a piece **uses** the builder inside its method body, not as a stored field



###