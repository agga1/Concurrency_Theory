defmodule IOfunc do
  @moduledoc false

  @doc """
  Wczytaniw pliku wejściowego i zamiana stringów na wygodniejszą reprezentację.
  """
  def parseInput(path) do
    {:ok, contents} = File.read(path)
    [alph_str, word_str | actions_str] = String.split(contents, "\r\n")

    alphabet = alph_str   |> parseAlphabet
    word =    word_str    |> parseWord
    actions = actions_str |> Enum.map(&parseAction/1)

    [alphabet, word , actions]
  end

  @doc """
  Wczytanie alfabetu: z postaci 'A = {a,b,c}' (lub 'A = {a, b, c})
  do postaci listy: [a,b,c]
  """
  def parseAlphabet(line) do
    [_A, seq] = String.split(line, " = ")
    seq
    |> String.split("")
    |> Enum.filter(fn(x) ->
      String.match?(x, ~r/^[[:alnum:]]+$/)
    end)
  end

  @doc """
  zmiana reprezentacji słowa:
  przekształca string 'w = aabc'
  jako listę: [a, a, b, c]
  """
  def parseWord(wordStr) do
    [_w, word] = String.split(wordStr, " = ")
    word |> String.split("", trim: true)
  end

  @doc """
  wygodniejsza reprezentacja transakcji:
  przekształca transakcje '(a) x = x + y'
  do formy: [a, x, {x,y}] (czyli: [nazwa transakcji, zmienna modyfikowana, zbiór zmiennych czytanych])
  """
  def parseAction(line) do
    [l, r] = String.split(line, "=")
    [nameP, modified] = String.split(l, " ", trim: true)
    ["(", name, ")"] = String.split(nameP, "", trim: true)
    refs = MapSet.new(r
                      |> String.split("", trim: true)
                      |> Enum.filter(fn(x) -> String.match?(x, ~r/^[[:alpha:]]+$/) end) # filter non-letters
    )
    [name, modified, refs]
  end

  @doc """
  formatowanie wyświetlania zbioru:
  postaci MapSet([{a,b}, {b,c}])
  jako Nazwa = {(a,b), (b,c)}
  """
  def displaySet(set, name) do
    pairs = set |> Enum.map(fn{a, b} -> "(#{a},#{b})" end) |> Enum.join(",")
    IO.puts("#{name} = {#{pairs}}")
  end

  @doc """
  wyświetla postać normalną Foaty.
  Przyjmuje klasy Foaty zapisane jako lista list
  np: [[b],[da],[a]]
  i wyświetla jako: 'FNF[w] = (b)(da)(a)'
  """
  def displayFoata(classes) do
    ans = classes |> Enum.map(&Enum.join(&1, "")) |> Enum.join(")(")
    IO.puts("FNF[w] = (#{ans})")
  end

  @doc """
  zapisuje graf dany jako @edges: listę krawędzi oraz @letterIds: listę par (nazwa_akcji, numer_wierzcholka_w_grafie)
  do formatu dot, jako plik "graph.dot"
  """
  def saveAsDotFile(edges, letterIds) do
    edgesStr = edges |> Enum.map(fn {from, to} -> "#{from} -> #{to}" end) |> Enum.join("\n")
    labelsStr = letterIds |> Enum.map(fn {label, id} -> "#{id}[label=#{label}]" end) |> Enum.join("\n")
    str = ["digraph g{", edgesStr, labelsStr, "}"] |> Enum.join("\n")
    File.write("graph.dot", str)
  end

end
