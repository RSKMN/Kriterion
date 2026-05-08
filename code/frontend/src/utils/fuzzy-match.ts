/**
 * A simple Levenshtein distance implementation for fuzzy string matching.
 */
export function getLevenshteinDistance(a: string, b: string): number {
  const matrix = Array.from({ length: a.length + 1 }, (_, i) =>
    Array.from({ length: b.length + 1 }, (_, j) => (i === 0 ? j : j === 0 ? i : 0))
  )

  for (let i = 1; i <= a.length; i++) {
    for (let j = 1; j <= b.length; j++) {
      if (a[i - 1] === b[j - 1]) {
        matrix[i][j] = matrix[i - 1][j - 1]
      } else {
        matrix[i][j] = Math.min(
          matrix[i - 1][j - 1] + 1, // replacement
          matrix[i][j - 1] + 1,     // insertion
          matrix[i - 1][j] + 1      // deletion
        )
      }
    }
  }

  return matrix[a.length][b.length]
}

/**
 * Returns a similarity score between 0 and 1.
 */
export function getSimilarity(a: string, b: string): number {
  const maxLength = Math.max(a.length, b.length)
  if (maxLength === 0) return 1.0
  return (maxLength - getLevenshteinDistance(a.toLowerCase(), b.toLowerCase())) / maxLength
}

/**
 * Finds the best match for a query in a list of targets.
 */
export function findBestMatch(query: string, targets: string[]): { target: string, score: number } {
  let bestMatch = { target: '', score: 0 }
  
  for (const target of targets) {
    const score = getSimilarity(query, target)
    if (score > bestMatch.score) {
      bestMatch = { target, score }
    }
  }
  
  return bestMatch
}
