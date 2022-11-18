package ch.epfl.tchu;

/**
 * Preconditions utiliser pour verifier les propreté d'autre méthode
 */
public final class Preconditions {
    private Preconditions() {}
    /**
     * Vérifie que argument passer en paramètres en Vrai , lance une exception sinon
     *
     * @param shouldBETrue argument vérifiée
     *
     * @throws IllegalArgumentException si expression est Fausse
     */
    public static void checkArgument(boolean shouldBETrue) {
        if (!shouldBETrue) {
            throw new IllegalArgumentException();
        }
    }
}

